#  Employee Scheduling System (Java and Python)

This project implements an Employee Scheduling System that assigns employees to shifts across a 7-day week while satisfying constraints such as shift preferences, maximum working days, and minimum staffing requirements.

It is implemented in both:
- Java (Swing GUI version)
- Python (Tkinter GUI version)


##  Features
- 7-day weekly schedule (Monday–Sunday)
- 3 shifts per day (Morning, Afternoon, Evening)
- Employee preference-based scheduling
- Maximum 5 working days per employee
- Minimum 2 employees per shift
- Conflict resolution system
- Randomized fairness in assignment
- GUI-based input and output

---

##  Project Structure

project-folder/
│
├── java-version/
│   └── EmployeeSchedulerGUI.java
│
├── python-version/
│   └── scheduler_gui.py
│
└── README.md



## Java Version (Swing GUI)

###  How to Run

Compile:
javac EmployeeSchedulerGUI.java

Run:
java EmployeeSchedulerGUI

###  Requirements
- Java JDK 8+

---

## Python Version (Tkinter GUI)

### How to Run

cd python-version
python EmployeeSchedulerGUI.py

or:
python3 EmployeeSchedulerGUI.py

###  Requirements
- Python 3.x
- Tkinter

---

##  How It Works
1. Add employees with preferences
2. System assigns shifts based on:
   - Preferences
   - Availability
   - Max 5 days rule
3. Resolves conflicts automatically
4. Ensures full shift coverage

---

## Future Improvements
- Database integration
- Web UI version
- Excel export
- AI-based optimization

---

## 👨‍💻 Author
Scheduling system Assignment

