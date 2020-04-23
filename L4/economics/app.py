from flask import Flask, jsonify, abort, make_response, request, render_template
import requests
import json

app = Flask(__name__)

expenses_db = [
    {
        'id': 1,
        'author': 'Marin',
        'description': 'Fruits',
        'value': 10
    },
    {
        'id': 2,
        'author': 'Ana',
        'description': 'Milk',
        'value': 6
    },
    {
        'id': 3,
        'author': 'Marin',
        'description': 'Water bill',
        'value': 30
    }
]

base_url = 'http://127.0.0.1:5000/'


@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)


@app.route('/economics/expenses', methods=['GET'])
def api_get_expenses():
    return jsonify({'expenses': expenses_db})


@app.route('/economics/expenses/<int:expense_id>', methods=['GET'])
def api_get_expense_by_id(expense_id):
    expense = [expense for expense in expenses_db if expense['id'] == expense_id]
    if 0 == len(expense):
        abort(404)
    return jsonify({'expense': expense[0]})


@app.route('/economics/expenses/<string:expense_author>', methods=['GET'])
def api_get_expenses_by_author(expense_author):
    expense = [expense for expense in expenses_db if expense['author'].lower() == expense_author.lower()]
    if 0 == len(expense):
        abort(404)
    return jsonify({'expenses': expense})


@app.route('/economics/expenses/', methods=['POST'])
def api_create_expense():
    if not request.json or 'value' not in request.json:
        abort(400)  # HTTP code for bad request
    expense_id = 1
    if 0 < len(expenses_db):
        expense_id = expenses_db[-1]['id'] + 1
    expense = {
        'id': expense_id,
        'author': request.json.get('author', 'unnamed'),
        'description': request.json.get('description', ''),
        'value': request.json['value']
    }
    expenses_db.append(expense)
    return jsonify({'expense': expense}), 201   # HTTP code for Created


@app.route('/economics/expenses/<int:expense_id>', methods=['PUT'])
def api_update_expense(expense_id):
    expense = [expense for expense in expenses_db if expense['id'] == expense_id]
    if 0 == len(expense):
        abort(404)  # HTTP code for Not Found
    if not request.json:
        abort(400)  # HTTP code for Bad Request
    if 'author' in request.json:
        expense[0]['author'] = request.json['author']
    if 'description' in request.json:
        expense[0]['description'] = request.json['description']
    if 'value' in request.json:
        expense[0]['value'] = request.json['value']
    return jsonify({'expense': expense[0]})


@app.route('/economics/expenses/<int:expense_id>', methods=['DELETE'])
def api_delete_expense(expense_id):
    expense = [expense for expense in expenses_db if expense['id'] == expense_id]
    if 0 == len(expense):
        abort(404)
    expenses_db.remove(expense[0])
    return jsonify({'result': True})


@app.route('/economics')
@app.route('/economics/index')
def index():
    response = requests.get('{}/economics/expenses'.format(base_url))
    data = json.loads(response.text)
    return render_template('index.html', expenses=data['expenses'])


@app.route('/economics/add_expense', methods=['POST'])
def add_expense():
    data = {
        'author': request.form.getlist('author')[0],
        'description': request.form.getlist('description')[0],
        'value': request.form.getlist('value')[0]
    }
    requests.post('{}/economics/expenses/'.format(base_url), json=data)
    return render_template('done.html')


@app.route('/economics/delete_expense', methods=['GET'])
def delete_expense():
    requests.delete('{}/economics/expenses/{}'.format(base_url, request.args.get('id')))
    return render_template('done.html')


@app.route('/economics/update_expense', methods=['POST'])
def update_expense():
    expense_id = request.form.getlist('id')[0]
    data = {
        'author': request.form.getlist('author')[0],
        'description': request.form.getlist('description')[0],
        'value': request.form.getlist('value')[0]
    }
    requests.put('{}/economics/expenses/{}'.format(base_url, expense_id), json=data)
    return render_template('done.html')


if __name__ == '__main__':
    app.run()