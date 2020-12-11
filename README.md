# GymFree: White Label Gym Capacity and Support Management Solution

## Video demo
See demo.mp4  

## Design doc
https://docs.google.com/document/d/1uu9pcQcXIuuVdY7h7U5I8rQOeTprAa513opAmlmBJmM/edit?usp=sharing

## Summary
This is an Android App meant to be a white label solution for gyms. Gyms can take this app and stick their branding on it to have it function as a capacity management system during COVID, or regular operations if they wish to maintain a standard of quality and care. The app provides functionality for booking gym timeslots, location management, as well as support for both members and admins.

## Screenshots
![Owner Dashboard](https://i.imgur.com/CGqmQik.png)
![Member Dashboard](https://i.imgur.com/MExEXT9.png)

## How to run
Run the app in Android Studio’s emulator Pixel 3 API 27. Login as “owner@example.com” to test the owner side, and “fake@example.com” to test the member side. Both have passwords set to “123456”. We used Google FireBase (FireStore and Authentication) as our backend service. There is no need to work with this. You are unable to create or delete users and admins as that runs on a local nodeJS server on our system.
