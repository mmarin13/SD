@ECHO OFF
ECHO Pornesc 100 de microservicii Bidder...
FOR /l %%x in (1, 1, 100) DO (
	start javaw -jar out\artifacts\BidderMicroservice_jar\BidderMicroservice.jar
	ECHO A pornit Bidder#%%x
)
PAUSE