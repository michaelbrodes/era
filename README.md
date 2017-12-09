# My Assignments (formerly Electronically Returned Assignments)
My Assignments is Senior Project at SIUE developed by Michael Rhodes, Cameron
Scott, Josh Raben, and Zachary Auer. The goal of the project is to allow 
professors to efficiently return assignments to their students via automatically 
mapping students to a photo copy of their assignments and uploading said photo 
copies to a remote website. The way we allow for this is by generating QR codes 
that uniquely identify students, attaching those QR codes to individual pages 
of an assignment, scanning those assignments with a photo copier, scanning 
those photo copies for QR codes, and grouping those photo copies to students. 

## Our progress thus far. 
Currently we have almost all functionality for the offline portion of our 
system. The remaining parts of the offline portion yet to be completed: 
ordering assignments pages into the order submitted by students, archiving the
offline database and the assignments saved in it at the end of a semester, and 
printing out QR codes onto labels. The remaining parts of the online portion: 
actually presenting assignments to students, archiving of the online database, 
and authentication/authorization of students who want to view their assignments.

## Usage 
Our current release JARs are located in the dist directory of this repository. 
There is a my-assignments-uploader which is the offline portion of our system 
and a my-assignments-server which is the online version of our system. Also 
included is a README.pdf which explains how to use the my-assignments-uploader. 
It is not recommend to use my-assignments-server currently. Unless you have the 
schema set up in a local instance of MariaDB, most likely the server app will 
try to attach to a database, fail at attaching to that database, and crash.    