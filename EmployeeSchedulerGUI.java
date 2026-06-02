import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Collections;
public class EmployeeSchedulerGUI extends JFrame {

    // =====================================
    // CONSTANTS
    // =====================================

    private static final String[] DAYS = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
    };

    private static final String[] SHIFTS = {
            "Morning",
            "Afternoon",
            "Evening"
    };

    private static final int MAX_DAYS_PER_WEEK = 5;
    private static final int MIN_EMPLOYEES_PER_SHIFT = 2;

    // =====================================
    // EMPLOYEE CLASS
    // =====================================

    static class Employee {

        String name;
        int daysWorked;

        // Day -> Ranked Preferences
        Map<String, List<String>> preferences;

        public Employee(String name) {
            this.name = name;
            this.daysWorked = 0;
            this.preferences = new HashMap<>();
        }
    }

    // =====================================
    // DATA STORAGE
    // =====================================

    private final ArrayList<Employee> employees =
            new ArrayList<>();

    private final Random random =
            new Random();

    // =====================================
    // GUI COMPONENTS
    // =====================================

    private JTextField nameField;

    private JComboBox<String>[] firstChoice;
    private JComboBox<String>[] secondChoice;
    private JComboBox<String>[] thirdChoice;

    private JTextArea outputArea;

    private DefaultListModel<String> employeeListModel;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public EmployeeSchedulerGUI() {

        setTitle("Employee Scheduler");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildGUI();
    }

    // =====================================
    // BUILD GUI
    // =====================================

    private void buildGUI() {

        setLayout(new BorderLayout());

        JPanel topPanel =
                new JPanel(new BorderLayout());

        JPanel inputPanel =
                new JPanel(new GridLayout(8, 4, 5, 5));

        inputPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Employee Preferences"));

        inputPanel.add(
                new JLabel("Employee Name"));

        nameField = new JTextField();

        inputPanel.add(nameField);

        inputPanel.add(new JLabel("1st Choice"));
        inputPanel.add(new JLabel("2nd Choice"));

        firstChoice = new JComboBox[7];
        secondChoice = new JComboBox[7];
        thirdChoice = new JComboBox[7];

        for (int i = 0; i < DAYS.length; i++) {

            inputPanel.add(
                    new JLabel(DAYS[i]));

            firstChoice[i] =
                    new JComboBox<>(SHIFTS);

            secondChoice[i] =
                    new JComboBox<>(SHIFTS);

            thirdChoice[i] =
                    new JComboBox<>(SHIFTS);

            inputPanel.add(firstChoice[i]);
            inputPanel.add(secondChoice[i]);
            inputPanel.add(thirdChoice[i]);
        }

        topPanel.add(
                inputPanel,
                BorderLayout.CENTER);

        add(topPanel,
                BorderLayout.NORTH);

        // =====================================
        // CENTER SECTION
        // =====================================

        employeeListModel =
                new DefaultListModel<>();

        JList<String> employeeList =
                new JList<>(employeeListModel);

        JScrollPane employeeScroll =
                new JScrollPane(employeeList);

        employeeScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Employees"));

        outputArea =
                new JTextArea();

        outputArea.setEditable(false);

        JScrollPane outputScroll =
                new JScrollPane(outputArea);

        outputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Schedule Output"));

        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        employeeScroll,
                        outputScroll);

        splitPane.setDividerLocation(250);

        add(splitPane,
                BorderLayout.CENTER);

        // =====================================
        // BUTTONS
        // =====================================

        JPanel buttonPanel =
                new JPanel();

        JButton addEmployeeButton =
                new JButton("Add Employee");

        JButton generateScheduleButton =
                new JButton("Generate Schedule");

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(generateScheduleButton);

        add(buttonPanel,
                BorderLayout.SOUTH);

        addEmployeeButton.addActionListener(
                e -> addEmployee());

        generateScheduleButton.addActionListener(
                e -> generateSchedule());
    }

    // =====================================
    // ADD EMPLOYEE
    // =====================================

    private void addEmployee() {

        String name =
                nameField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter an employee name.");

            return;
        }

        Employee employee =
                new Employee(name);

        for (int i = 0; i < DAYS.length; i++) {

            String first =
                    (String) firstChoice[i]
                            .getSelectedItem();

            String second =
                    (String) secondChoice[i]
                            .getSelectedItem();

            String third =
                    (String) thirdChoice[i]
                            .getSelectedItem();

            // Ensure rankings are unique
            HashSet<String> unique =
                    new LinkedHashSet<>();

            unique.add(first);
            unique.add(second);
            unique.add(third);

            ArrayList<String> rankings =
                    new ArrayList<>(unique);

            // Fill missing choices if duplicates existed
            for (String shift : SHIFTS) {

                if (!rankings.contains(shift)) {
                    rankings.add(shift);
                }
            }

            employee.preferences.put(
                    DAYS[i],
                    rankings);
        }

        employees.add(employee);

        employeeListModel.addElement(
                employee.name);

        nameField.setText("");

        JOptionPane.showMessageDialog(
                this,
                employee.name + " added successfully.");
    }
    // =====================================
// GENERATE SCHEDULE
// =====================================

private void generateSchedule() {

    // reset
    for (Employee e : employees) {
        e.daysWorked = 0;
    }

    if (employees.size() < 9) {
        JOptionPane.showMessageDialog(
                this,
                "At least 9 employees required.");
        return;
    }

    // Day -> Shift -> Employees
    Map<String, Map<String, ArrayList<String>>> schedule =
            new LinkedHashMap<>();

    for (String day : DAYS) {

        Map<String, ArrayList<String>> shiftMap =
                new LinkedHashMap<>();

        for (String shift : SHIFTS) {
            shiftMap.put(shift, new ArrayList<>());
        }

        schedule.put(day, shiftMap);
    }

    Collections.shuffle(employees, random);

    // =====================================
    // PASS 1: PREFERENCES (RANKED)
    // =====================================

    for (String day : DAYS) {

        for (Employee e : employees) {

            if (e.daysWorked >= MAX_DAYS_PER_WEEK)
                continue;

            if (isAssignedToday(schedule, day, e.name))
                continue;

            boolean assigned = false;

            List<String> prefs = e.preferences.get(day);

if (prefs == null) continue;

// ensure only top 3 are used safely
for (int rank = 0; rank < Math.min(3, prefs.size()); rank++) {

    String shift = prefs.get(rank);
                ArrayList<String> workers =
                        schedule.get(day).get(shift);

                if (workers.size() < MIN_EMPLOYEES_PER_SHIFT) {

                    workers.add(e.name);
                    e.daysWorked++;
                    assigned = true;
                    break;
                }
            }

            // =====================================
            // IF NOT ASSIGNED → USE CONFLICT RESOLUTION
            // =====================================

            if (!assigned) {
                assignAlternativeShift(schedule, e, day);
            }
        }
    }

    // =====================================
    // PASS 2: FILL SHORTAGES
    // =====================================

    fillStaffShortages(schedule);

    displaySchedule(schedule);

    JOptionPane.showMessageDialog(
            this,
            "Schedule generated successfully.");
}

// =====================================
// CONFLICT RESOLUTION (REAL USAGE)
// =====================================

private boolean assignAlternativeShift(
        Map<String, Map<String, ArrayList<String>>> schedule,
        Employee e,
        String currentDay) {

    int dayIndex = getDayIndex(currentDay);

    // 1. TRY SAME DAY ANY SHIFT
    for (String shift : SHIFTS) {

        ArrayList<String> workers =
                schedule.get(currentDay).get(shift);

        if (workers.size() < MIN_EMPLOYEES_PER_SHIFT) {

            workers.add(e.name);
            e.daysWorked++;
            return true;
        }
    }

    // 2. TRY NEXT DAYS
    for (int i = dayIndex + 1; i < DAYS.length; i++) {

        String nextDay = DAYS[i];

        if (isAssignedToday(schedule, nextDay, e.name))
            continue;

        for (String shift : SHIFTS) {

            ArrayList<String> workers =
                    schedule.get(nextDay).get(shift);

            if (workers.size() < MIN_EMPLOYEES_PER_SHIFT) {

                workers.add(e.name);
                e.daysWorked++;
                return true;
            }
        }
    }

    return false;
}
// =====================================
// FILL STAFF SHORTAGES (RANDOM + VALID)
// =====================================

private void fillStaffShortages(
        Map<String, Map<String, ArrayList<String>>> schedule) {

    for (String day : DAYS) {

        for (String shift : SHIFTS) {

            ArrayList<String> workers =
                    schedule.get(day).get(shift);

            while (workers.size() < MIN_EMPLOYEES_PER_SHIFT) {

                Employee candidate =
                        findAvailableEmployee(schedule, day);

                if (candidate == null)
                    break;

                workers.add(candidate.name);
                candidate.daysWorked++;
            }
        }
    }
}

// =====================================
// FIND AVAILABLE EMPLOYEE (VALID ONLY)
// =====================================

private Employee findAvailableEmployee(
        Map<String, Map<String, ArrayList<String>>> schedule,
        String day) {

    ArrayList<Employee> available = new ArrayList<>();

    for (Employee e : employees) {

        if (e.daysWorked >= MAX_DAYS_PER_WEEK)
            continue;

        if (isAssignedToday(schedule, day, e.name))
            continue;

        available.add(e);
    }

    if (available.isEmpty())
        return null;

    return available.get(random.nextInt(available.size()));
}

// =====================================
// CHECK IF EMPLOYEE ALREADY WORKS THAT DAY
// =====================================

private boolean isAssignedToday(
        Map<String, Map<String, ArrayList<String>>> schedule,
        String day,
        String employeeName) {

    for (String shift : SHIFTS) {

        if (schedule.get(day)
                .get(shift)
                .contains(employeeName)) {
            return true;
        }
    }

    return false;
}

// =====================================
// GET DAY INDEX
// =====================================

private int getDayIndex(String day) {

    for (int i = 0; i < DAYS.length; i++) {
        if (DAYS[i].equals(day))
            return i;
    }

    return -1;
}

// =====================================
// DISPLAY SCHEDULE
// =====================================

private void displaySchedule(
        Map<String, Map<String, ArrayList<String>>> schedule) {

    StringBuilder sb = new StringBuilder();

    sb.append("FINAL WEEKLY SCHEDULE\n\n");

    for (String day : DAYS) {

        sb.append(day).append("\n");

        for (String shift : SHIFTS) {

            sb.append("  ")
              .append(shift)
              .append(": ")
              .append(schedule.get(day).get(shift))
              .append("\n");
        }

        sb.append("\n");
    }

    sb.append("========================\n");
    sb.append("EMPLOYEE SUMMARY\n");
    sb.append("========================\n");

    for (Employee e : employees) {

        sb.append(e.name)
          .append(" -> ")
          .append(e.daysWorked)
          .append(" days worked\n");
    }

    outputArea.setText(sb.toString());
}

// =====================================
// MAIN METHOD
// =====================================

public static void main(String[] args) {

    SwingUtilities.invokeLater(() -> {

        EmployeeSchedulerGUI gui =
                new EmployeeSchedulerGUI();

        gui.setVisible(true);
    });
}
}