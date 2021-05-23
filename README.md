# museum-visit-summarizer
A web application which makes a user to replay his/her visit of the [Haifa Museum](https://www.hma.org.il/eng).
## Repository Content
### Preprocessing script
The **log-to-json.py** script makes us to extract the data from the `Museum data` folder and to organize them in a structed format as a `json` file. In particular, 
it creates a collection of *Visitor* objects and associates to them a *group number*, a list of *positions of interest (POIs)* and a list of *presentations* with attributes. It also filters out the *Visitor* objects which do not have any recorded movements among POIs.
### Web application
The **acme-museum** engine makes you replay a visit of the [Haifa Museum](https://www.hma.org.il/eng) of a certain visitor of group. The input of the application is the 
`data.json` file, created by the **Post-Processing** script, which on turns extracts informations from the `Museum Data` directory. The file is read internally by the Web App, a it makes us to populate the database. However, it is also possible to upload it from the user interface.

Given a certain visitor or group (indentified by a number), it constructs two distict **networks**, representing the movements of the visitor/group among positions of interest (**POIs**) (where sensors captures their movements) and among the museum rooms, visualized as *force-directed graph* through the **d3.js** library (pictures below). A user can interact with the networks by dragging and moving the positions or the rooms.

The engine makes us also to visualize the **statistics** of a certain visit. Namely, if the visitor stayed more/less than average, if he/she enjoyed or interruped presentations. Moreover, it computes also the general statistics of the Haifa Museum for a certain day: (i) average time spent by the visitors/groups; (ii) number of visitors per hour (iii) number of visitors per room per hour.

The exectuable file (.jar) of the Web App can be downloaded [here](https://drive.google.com/file/d/1c6hdJKcqEGeAgQrlpvVt5k_Wi1UKNT_e/view?usp=sharing) (usage and prerequisited described below).

### Position Graph
![picture](examplePositionGraph.PNG)

### Room Graph
![picture](exampleRoomGraph.PNG)

## Prerequisites
- For running the **Web App** you need to have [Java 1.8.0](https://www.oracle.com/it/java/technologies/javase-downloads.html) (or upper version) installed 
(you need to be able to run the command  `java -jar` on an excecutable file).
- For running the **log_to_json.py** script, in order to process the Museum Log and extract a json file with all the visitors data organized, you
need to have a `python` version installed (you need to be able to excecute a python script).

## Usage
### Launching the Web App
There are two ways to launch the Web App. You need to do the following two steps:
1. You can either (i) download the executable `.jar` file from [here](https://drive.google.com/file/d/1c6hdJKcqEGeAgQrlpvVt5k_Wi1UKNT_e/view?usp=sharing)
**OR** (ii) generate it using [maven](https://maven.apache.org/) by navigating inside the `acme-museum` folder and typing
the command `mvn package` to compile and create the exectuable file.
2. After you have the executable `.jar` file, navigate to the folder where the `acme-museum-1.0.0.jar` is located, open the terminal and type the command `java -jar acme-museum-1.0.0.jar`. Then, wait for the database initialization and connect your browser to `localhost:8080`.

### Launching the preprocessing script 
If you want to generate the single `data.json` file, which contains all the museum log informations organized and ready to be processed,
you need to do the following steps. 
1. Navigate to the folder wher the script `log_to_json.py` is located. 
2. Then, type the following command `python log_to_json.py <"argument1"> <"argument2"> <"argument2">`, where `argument1` is the
path of the folder where you want to locate the json file, `argument2` is the name of the json file you want to create (must end with .json) and 
`argument2` is the path to the log directory (e.g. *'Museum data/Logs/'*). 
Alternatevely, if no argument is provided the following arguments are launched by default: `python log_to_json.py 'Museum data/' 'data.json' 'Museum data/Logs/'`
