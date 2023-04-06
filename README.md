<div align="center">

  <img src="README Assets/eye.gif" alt="logo" width="auto" height="auto" />
  <h1>VirtualEYE</h1>
  
  <p>
    HELPING THE VISUALLY IMPAIRED MAP THE REAL WORLD 
  </p>
  
  
<!-- Badges -->
<p>
  <a href="https://github.com/aish21/VirtualEYE-FYP/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/aish21/VirtualEYE-FYP" alt="contributor" />
  </a>
  <a href="">
    <img src="https://img.shields.io/github/last-commit/aish21/VirtualEYE-FYP" alt="last commit" />
  </a>
  <a href="https://github.com/aish21/VirtualEYE-FYP/network/members">
    <img src="https://img.shields.io/github/forks/aish21/VirtualEYE-FYP" alt="forks" />
  </a>
  <a href="https://github.com/aish21/VirtualEYE-FYP/stargazers/">
    <img src="https://img.shields.io/github/stars/aish21/VirtualEYE-FYP" alt="stars" />
  </a>
  <a href="https://github.com/aish21/VirtualEYE-FYP/issues/">
    <img src="https://img.shields.io/github/issues/aish21/VirtualEYE-FYP" alt="open issues" />
  </a>
  <a href="https://github.com/aish21/VirtualEYE-FYP/blob/master/LICENSE/">
    <img src="https://img.shields.io/github/license/aish21/VirtualEYE-FYP.svg" alt="license" />
  </a>
</p>
   
<h4>
    <a href="FYP Reports/SINGH-AISHWARYA_U1923952C_SCSE22-0364.pdf">Documentation</a>
  <span> · </span>
    <a href="https://github.com/aish21/VirtualEYE-FYP/issues/">Report Bugs</a>
  <span> · </span>
    <a href="https://github.com/aish21/VirtualEYE-FYP/issues/">Request Feature</a>
  </h4>
</div>

<br />

<!-- Table of Contents -->
# :notebook_with_decorative_cover: Table of Contents

- [About the Project](#star2-about-the-project)
  * [Video DemonStration](#video-demo)
  * [Screenshots](#camera-screenshots)
  * [Tech Stack](#space_invader-tech-stack)
  * [Features](#dart-features)
- [Roadmap](#compass-roadmap)
- [Contact](#handshake-contact)  
- [Acknowledgements](#gem-acknowledgements)

<!-- About the Project -->
## :star2: About the Project
This project discusses the design and implementation of an enhanced indoor navigation system to improve existing technology by aiding the visually impaired. Loss of vision can drastically impair an individual's sense of direction and mobility, especially in unfamiliar surroundings. Due to this, visually impaired individuals often find themselves needing further support and time to gain familiarity with new indoor settings. This report aims to present an enhanced indoor navigation system, customized to cater to the needs of the visually impaired. This has been achieved through the use of Bluetooth Low Energy (BLE) beacons, a BLE-supported Android device with in-built motion sensors, and an Android Mobile Application.

The mobile application can be operated in three different modes – regular navigation, assisted navigation, and free-roam mode. The current implementation
of the application supports navigation at Level 1 of the School of Computer Science and Engineering (SCSE). BLE beacons are placed at important landmarks of this environment, and the user is provided with a list of destinations for navigation. User interaction, feedback, and the effectiveness of the proposed indoor navigation system have been evaluated in the report. Experimental results and observations indicate an improvement in indoor navigation with the introduction of VirtualEYE, the Android Mobile Application.

### 📹 Video Demo
Video Demonstration Link: [Video Link](https://youtu.be/6d57YgNWh-w)

<!-- Screenshots -->
### :camera: Screenshots

<div align="center"> 
  <img src="README Assets/ss.jpeg" alt="screenshot" />
  <img src="README Assets/ss2.jpeg" alt="screenshot" />
  <img src="README Assets/ss3.jpeg" alt="screenshot" />
  <img src="README Assets/ss4.jpeg" alt="screenshot" />
  <img src="README Assets/ss5.jpeg" alt="screenshot" />
  <img src="README Assets/ss6.jpeg" alt="screenshot" />
  <img src="README Assets/ss7.jpeg" alt="screenshot" />
  <img src="README Assets/ss8.jpeg" alt="screenshot" />
</div>


<!-- TechStack -->
### :space_invader: Tech Stack and Tools

<details>
  <summary>Client</summary>
  <ul>
    <li><a href="https://developer.android.com/">Android Studio</a></li>
    <li><a href="https://www.tensorflow.org/lite/">TensorFlow Lite</a></li>
    <li><a href="https://firebase.google.com/docs/ml-kit/">Firebase ML Kit</a></li>
    <li><a href="https://sketchfab.com/">SketchFab</a></li>
  </ul>
</details>

<details>
  <summary>Server</summary>
  <ul>
    <li><a href="https://code.visualstudio.com/">Visual Studio</a></li>
    <li><a href="https://www.docker.com/">Docker</a></li>
    <li><a href="https://azure.microsoft.com/en-us/">Azure Cloud</a></li>
    <li><a href="https://www.python.org/">Python</a></li>
    <li><a href="https://flask.palletsprojects.com/en/2.2.x//">Flask</a></li>
  </ul>
</details>

<!-- Features -->
### :dart: Features

- Receive Instructions/Commands from the user via:
  * User Interface (UI) input
  * Kinetic input
  * Voice input
- Interact with the BLE beacons placed around the test area to:
  * Retrieve the landmark in the closest proximity to the user
  * Calculate the distance between the user and the retrieved landmark
- Perform obstacle detection to:
  * Warn the user under assisted navigation of obstacles in the path
- Provide Tactile and Audio feedback for the visually impaired users:
  * Vibrate the phone to indicate the correct direction during navigation
  * Give audio warnings and instructions using Text-To-Speech (TTS) to provide easier interaction with the application
- Display an interactable map of the test area for visual navigation:
  * Present a list of available locations the user can select and navigate to
  * Update the map with markers of selected locations and the path calculated
  * Provide written directions for navigation

<!-- Roadmap -->
## :compass: Roadmap

* [x] Work on Server Side code - Shortest Path calculation
* [x] Work on Server Side code - Create Docker Image
* [x] Work on Server Side code - Push server to cloud
* [x] Work on Client Side code - Create Indoor Map
* [x] Work on Client Side code - Test BLE Connections
* [x] Work on Client Side code - Create proximity estimation algorithm
* [x] Work on Client Side code - Work on Obstacle Detection
* [x] Work on Client Side code - Create voice input and recognition
* [x] Work on Client Side code - Create navigation algorithm
* [x] Set up Client-Server communication
* [x] Test pipeline
* [ ] TODO: Video Demonstration
* [ ] TODO: Multi-floor navigation
* [ ] TODO: AR-Navigation Feature
* [ ] TODO: Proximity Estimation for Obstacles


<!-- Contact -->
## :handshake: Contact

Aishwarya Singh - [Linkedin](https://www.linkedin.com/in/aishwarya-singh-547092174/)

Project Link: [GitHub Repository](https://github.com/aish21/VirtualEYE-FYP)

<!-- Acknowledgments -->
## :gem: Acknowledgements

The author would like to thank her advisor and mentor Dr. Smitha Kavallur Pisharath Gopi for her guidance and support throughout the entirety of the research.
Her advice and constructive feedback were invaluable in shaping the direction and quality of the work presented in this report.
