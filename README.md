# Football stats
Files with settings you MUST update:
- api.sports.properties OR rapid.api.properties (pass YOUR token there)
- settings.properties - define there at least your API provider and spreadsheet.id
- credentials.json - this one related to your Google account, please see https://developers.google.com/sheets/api/quickstart/java
  (steps from 'Enable the API' to 'Save the downloaded JSON file as credentials.json, and move the file to your working directory'.)
- leagues.json & bookmaker.json - Update id's for new leagues and bookmakers if you plan to expand your list
# How to install Java on MacOS:
1. Install Homebrew:
- Open Terminal.
- Copy and paste the following command into the Terminal:
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
- Press Enter and follow the instructions provided by the installer.
- Wait for the installation to complete. Homebrew will be installed in /usr/local directory.



2. Install Java 11 using Homebrew:
- In Terminal, enter the following command to update Homebrew to the latest version:
```
brew update
```
- Once Homebrew is updated, use the following command to install Java 11:
```
brew install openjdk@11
```
- Wait for the installation to complete. Java 11 will be installed on your system.


# How to install Java on Windows:
https://www.youtube.com/watch?v=dgiQyx13X0M

# How to run
1. Go to main directory football-stats
2. Run command ./gradlew run

# How to pass properties from command line
just use -D option to override what is written in settings.xml
Example:
```agsl
./gradlew run -Dleague=MLS -Dseason=2024
```