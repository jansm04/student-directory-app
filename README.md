## Student Directory Mobile Application

### Overview

This is a simple directory application that fetches data from a URL and displays it to the user. The app features a map view powered by the Google Maps API to show each student's location and a web view of the home site. Each record of a student consists of a name, ID, address, location, phone number, and image. Users also have the ability to call the user directly through their personal page, if they allow the app permission to do so.

### HTTPS Requests

The app fetches student data from a secure external URL. The request is sent each time the user first opens the application, and requires authentication to access successfully. This authorization is handled by an authentication interceptor class that adds valid credentials to the request header each time the request is sent.

### Offline Storage

The app features a button to start and stop a service that 1) stores student data into an SQLite database every 5 seconds, and 2) tries to post the database records to an API endpoint every 20 seconds. The base URL is the same one used to fetch the student data, so each POST request also requires authorization. If the data is successfully posted, then the service deletes the posted records from the SQLite database. If the request is unsuccessful, the data remains stored. As running the service while offline can pile up the database, the POST request is limited to 1000 records at a time. This serves to prevent the system from querying, posting and deleting a large number of records at a single time, effectively keeping the app from crashing.

### Dependencies
- [OkHttp](https://square.github.io/okhttp/)
- [Retrofit](https://square.github.io/retrofit/)
- [Picasso](https://square.github.io/picasso/)
- [Tinylog](https://tinylog.org/v2/)
