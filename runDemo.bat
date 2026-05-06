@echo off
cd /d C:\Users\lovep\IdeaProjects\CSC325-Project

"C:\Program Files\Java\jdk-25.0.2\bin\java.exe" --module-path "C:\Users\lovep\.m2\repository\org\openjfx\javafx-base\21.0.6;C:\Users\lovep\.m2\repository\org\openjfx\javafx-controls\21.0.6;C:\Users\lovep\.m2\repository\org\openjfx\javafx-graphics\21.0.6;C:\Users\lovep\.m2\repository\org\openjfx\javafx-fxml\21.0.6;C:\Users\lovep\.m2\repository\org\openjfx\javafx-media\21.0.6" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "target\classes;C:\Users\lovep\.m2\repository\*" edu.farmingdale.demo1.Launcher

pause