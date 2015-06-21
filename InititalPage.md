# Introduction #

We are going to make a distributed system to watch the states of a wide variety of sensors no matter of the place their are situated


# Details #
The basics of the “tControl” project. The draft of system analysis documents.

The way of the system works.

  1. User starts app;
  1. User selects the sensors to read their states;
  1. User sends the query within the app;
  1. User gets the states of devices on screen of the app.

The technical description of the system:
  1. App creates a query to the DB based on user’s selection;
  1. App transforms a query into JSON;
  1. Java app gets JSON from mobile app;
  1. Java app asks DB of the sensors state (for now - only recent state, any other dates and conditions - further);
  1. Java app gets the answer from the DB;
  1. Java app transforms the answer into JSON and sends it back to the mobile app;
  1. Mobile app transforms JSON into viewable forms on s screen.


Algorithm of a mobile application’s work:
  1. apps logon
  1. apps requests sensors
  1. apps renders sensors
  1. apps requests sensors states
  1. apps shows sensors stated
  1. apps repeats 4,5 each 5 minutes (timeout is adjustable)

IMPORTANT: No user actions should be supported for now (to be developed).