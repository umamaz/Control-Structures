import tkinter as tk
from tkinter import messagebox
import random

DAYS = [
    "Monday", "Tuesday", "Wednesday",
    "Thursday", "Friday", "Saturday", "Sunday"
]

SHIFTS = ["Morning", "Afternoon", "Evening"]

MAX_DAYS = 5
MIN_PER_SHIFT = 2


class Employee:
    def __init__(self, name):
        self.name = name
        self.days_worked = 0
        self.preferences = {}


class SchedulerGUI:

    def __init__(self, root):
        self.root = root
        self.root.title("Employee Scheduler")

        self.employees = []

        # ---------------- NAME INPUT ----------------
        tk.Label(root, text="Employee Name").grid(row=0, column=0)
        self.name_entry = tk.Entry(root)
        self.name_entry.grid(row=0, column=1)

        # ---------------- PREFERENCE DROPDOWNS ----------------
        self.pref_vars = {}  # day -> list of 3 dropdowns

        for i, day in enumerate(DAYS):
            tk.Label(root, text=day).grid(row=i + 1, column=0)

            self.pref_vars[day] = []

            for j in range(3):
                var = tk.StringVar(value=SHIFTS[j % 3])
                dropdown = tk.OptionMenu(root, var, *SHIFTS)
                dropdown.grid(row=i + 1, column=j + 1)
                self.pref_vars[day].append(var)

        # ---------------- BUTTONS ----------------
        tk.Button(root, text="Add Employee",
                  command=self.add_employee).grid(row=8, column=0)

        tk.Button(root, text="Generate Schedule",
                  command=self.generate_schedule).grid(row=8, column=1)

        # ---------------- OUTPUT ----------------
        self.output = tk.Text(root, width=90, height=25)
        self.output.grid(row=9, column=0, columnspan=4)

    # ---------------- ADD EMPLOYEE ----------------
    def add_employee(self):

        name = self.name_entry.get().strip()

        if not name:
            messagebox.showerror("Error", "Enter employee name")
            return

        emp = Employee(name)

        for day in DAYS:

            prefs = [
                self.pref_vars[day][0].get(),
                self.pref_vars[day][1].get(),
                self.pref_vars[day][2].get()
            ]

            # remove duplicates while preserving order
            seen = set()
            clean = []

            for p in prefs:
                if p not in seen:
                    clean.append(p)
                    seen.add(p)

            emp.preferences[day] = clean

        self.employees.append(emp)
        self.name_entry.delete(0, tk.END)

        messagebox.showinfo("Added", f"{name} added successfully")

    # ---------------- GENERATE ----------------
    def generate_schedule(self):

        if len(self.employees) < 9:
            messagebox.showerror("Error", "Need at least 9 employees")
            return

        for e in self.employees:
            e.days_worked = 0

        schedule = {
            day: {shift: [] for shift in SHIFTS}
            for day in DAYS
        }

        random.shuffle(self.employees)

       
        for day in DAYS:

            day_employees = self.employees[:]
            random.shuffle(day_employees)

            for e in day_employees:

                if e.days_worked >= MAX_DAYS:
                    continue

                if self.is_assigned(schedule, day, e.name):
                    continue

                assigned = False
                prefs = e.preferences.get(day, SHIFTS)

                for shift in prefs:

                    if len(schedule[day][shift]) < MIN_PER_SHIFT:
                        schedule[day][shift].append(e.name)
                        e.days_worked += 1
                        assigned = True
                        break

                if not assigned:
                    self.fallback(schedule, e, day)

        self.fill_shortages(schedule)

        self.display(schedule)

    # ---------------- FALLBACK ----------------
    def fallback(self, schedule, e, day):

        for shift in SHIFTS:

            if len(schedule[day][shift]) < MIN_PER_SHIFT:
                schedule[day][shift].append(e.name)
                e.days_worked += 1
                return

        idx = DAYS.index(day)

        for i in range(idx + 1, len(DAYS)):
            nxt = DAYS[i]

            if self.is_assigned(schedule, nxt, e.name):
                continue

            for shift in SHIFTS:

                if len(schedule[nxt][shift]) < MIN_PER_SHIFT:
                    schedule[nxt][shift].append(e.name)
                    e.days_worked += 1
                    return

    # ---------------- FILL SHORTAGES ----------------
    def fill_shortages(self, schedule):

        for day in DAYS:
            for shift in SHIFTS:

                while len(schedule[day][shift]) < MIN_PER_SHIFT:

                    emp = self.find_employee(schedule, day)

                    if not emp:
                        return

                    schedule[day][shift].append(emp.name)
                    emp.days_worked += 1

    def find_employee(self, schedule, day):

        available = [
            e for e in self.employees
            if e.days_worked < MAX_DAYS
            and not self.is_assigned(schedule, day, e.name)
        ]

        return random.choice(available) if available else None

    # ---------------- CHECK ----------------
    def is_assigned(self, schedule, day, name):

        return any(name in schedule[day][s] for s in SHIFTS)

    # ---------------- OUTPUT ----------------
    def display(self, schedule):

        self.output.delete("1.0", tk.END)

        self.output.insert(tk.END, "FINAL WEEKLY SCHEDULE\n\n")

        for day in DAYS:

            self.output.insert(tk.END, day + "\n")

            for shift in SHIFTS:
                self.output.insert(
                    tk.END,
                    f"  {shift}: {schedule[day][shift]}\n"
                )

            self.output.insert(tk.END, "\n")

        self.output.insert(tk.END, "========================\nEMPLOYEE SUMMARY\n========================\n")

        for e in self.employees:
            self.output.insert(
                tk.END,
                f"{e.name} -> {e.days_worked} days\n"
            )


# ---------------- RUN ----------------
if __name__ == "__main__":
    root = tk.Tk()
    app = SchedulerGUI(root)
    root.mainloop()
