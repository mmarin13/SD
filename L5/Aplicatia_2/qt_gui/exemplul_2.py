import os
import sys
from PyQt5.QtWidgets import QWidget, QApplication, QFileDialog, QMessageBox
from PyQt5 import QtCore
from PyQt5.uic import loadUi
from mq_communication import RabbitMq


def debug_trace(ui=None):
    from pdb import set_trace
    QtCore.pyqtRemoveInputHook()
    set_trace()
    # QtCore.pyqtRestoreInputHook()


class LibraryApp(QWidget):
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

    def __init__(self):
        super(LibraryApp, self).__init__()
        ui_path = os.path.join(self.ROOT_DIR, 'exemplul_2.ui')
        loadUi(ui_path, self)
        self.search_btn.clicked.connect(self.search)
        self.save_as_file_btn.clicked.connect(self.save_as_file)
        self.add_btn.clicked.connect(self.showNewBookWin)
        self.rabbit_mq = RabbitMq(self)

        self.widgetNewBook = QWidget()
        ui_path = os.path.join(self.ROOT_DIR, 'window_add_book.ui')
        loadUi(ui_path, self.widgetNewBook)
        self.widgetNewBook.confirm_btn.clicked.connect(self.addBook)

    def set_response(self, response):
        self.result.setText(response)

    def send_request(self, request):
        self.rabbit_mq.send_message(message=request)
        print("mesaj trimis")
        self.rabbit_mq.receive_message()
        print("mesaj primit")

    def search(self):
        search_string = self.search_bar.text()
        request = None
        if self.json_rb.isChecked():
            request = 'print::json'
        elif self.html_rb.isChecked():
            request = 'print::html'
        elif self.xml_rb.isChecked():
            request = 'print::xml'
        else:
            request = 'print::raw'
        if not search_string:
            request += '&&find::all'
        else:
            if self.author_rb.isChecked():
                request += '&&find::author={}'.format(search_string)
            elif self.title_rb.isChecked():
                request += '&&find::title={}'.format(search_string)
            else:
                request += '&&find::publisher={}'.format(search_string)
        self.send_request(request)

    def save_as_file(self):
        options = QFileDialog.Options()
        options |= QFileDialog.DontUseNativeDialog
        file_path = str(
            QFileDialog.getSaveFileName(self, 'Salvare fisier', options=options))
        if file_path:
            file_path = file_path.split("'")[1]
            if not file_path.endswith('.json') and not file_path.endswith('.html') and not file_path.endswith('.txt') \
                    and not file_path.endswith('.xml'):
                if self.json_rb.isChecked():
                    file_path += '.json'
                elif self.html_rb.isChecked():
                    file_path += '.html'
                elif self.xml_rb.isChecked():
                    file_path += '.xml'
                else:
                    file_path += '.txt'
            try:
                with open(file_path, 'w') as fp:
                    if file_path.endswith('.html'):
                        fp.write(self.result.toHtml())
                    elif file_path.endswith('.json'):
                        fp.write(self.toJson())
                    elif file_path.endswith('.xml'):
                        fp.write(self.toXml())
                    else:
                        fp.write(self.result.toPlainText())
            except Exception as e:
                print(e)
                QMessageBox.warning(self, 'LibraryApp', 'Nu s-a putut salva fisierul')

    def toJson(self):
        plainResult = self.result.toPlainText()
        elements = ['"Titlu": ', '"Autor": ', '"Editura": ', '"Text": ']
        jsonResult = list('[\n')
        firstFlag = True

        position = -1
        index = 0
        while position < len(plainResult):
            prevPosition = position + 1
            position = plainResult.find('\n', prevPosition)

            if -1 == position:
                break

            if not plainResult[prevPosition:position]:
                continue

            if 0 == index:
                if firstFlag:
                    jsonResult.append('\t{')
                    firstFlag = False
                else:
                    jsonResult.append(',\n\t{')

            jsonResult.append(elements[index])
            jsonResult.append('"')
            jsonResult.append(plainResult[prevPosition:position])
            if len(elements) - 1 == index:
                jsonResult.append('"}')
            else:
                jsonResult.append('", ')

            index = (index + 1) % len(elements)

        jsonResult.append('\n]\n')

        return ''.join(jsonResult)

    def toXml(self):
        plainResult = self.result.toPlainText()
        elements = ['\t\t<titlu>', '\t\t<autor>', '\t\t<editura>', '\t\t<text>',
                    '</titlu>\n', '</autor>\n', '</editura>\n', '</text>\n']
        jsonResult = list('<carti>')

        position = -1
        index = 0
        while position < len(plainResult):
            prevPosition = position + 1
            position = plainResult.find('\n', prevPosition)

            if -1 == position:
                break

            if not plainResult[prevPosition:position]:
                continue

            if 0 == index:
                jsonResult.append('\n\t<carte>\n')

            jsonResult.append(elements[index])
            jsonResult.append(plainResult[prevPosition:position])
            jsonResult.append(elements[index + len(elements) // 2])

            if (len(elements) // 2 - 1) == index:
                jsonResult.append('\t</carte>')
                index = 0
            else:
                index = index + 1

        jsonResult.append('\n</carti>\n')

        return ''.join(jsonResult)

    def showNewBookWin(self):
        self.widgetNewBook.show()

    def addBook(self):
        author = self.widgetNewBook.textEditAutor.toPlainText().strip()
        title = self.widgetNewBook.textEditTitlu.toPlainText().strip()
        publisher = self.widgetNewBook.textEditEditura.toPlainText().strip()
        text = self.widgetNewBook.textEditText.toPlainText().strip()

        if not author or not title or not publisher or not text:
            popupWin = QMessageBox()
            popupWin.setWindowTitle('Atentie')
            popupWin.setText('Toate campurile sunt obligatorii!')
            popupWin.exec()
            return

        request = 'add::author={};;title={};;publisher={};;text={}'.format(author, title, publisher, text)
        self.send_request(request)

        self.widgetNewBook.close()


if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = LibraryApp()
    window.show()
    sys.exit(app.exec_())
