import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.ImageIcon;

public class ListifyApp2 {
   // Task class representing individual tasks
    static class Task {
        String description;
        boolean isCompleted;
        String category;
        LocalTime reminderTime;

        Task(String description, String category, LocalTime reminderTime) {
            this.description = description;
            this.category = category;
            this.isCompleted = false;
            this.reminderTime = reminderTime;
        }

        void toggleCompleted() {
            isCompleted = !isCompleted;
        }

        @Override
        public String toString() {
            return (isCompleted ? "[✔] " : "[ ] ") + description +
                    (reminderTime != null ? " (Reminder: " + reminderTime.format(DateTimeFormatter.ofPattern("hh:mm a")) + ")" : "");
        }
    }

    // Lists to store tasks categorized by type
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final DefaultListModel<Task> personalTasks = new DefaultListModel<>();
    private final DefaultListModel<Task> schoolTasks = new DefaultListModel<>();
    private final DefaultListModel<Task> workTasks = new DefaultListModel<>();
    private final DefaultListModel<Task> otherTasks = new DefaultListModel<>();
    private final DefaultListModel<Task> finishedTasks = new DefaultListModel<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // UI Components
    private final JList<Task> personalList = new JList<>(personalTasks);
    private final JList<Task> schoolList = new JList<>(schoolTasks);
    private final JList<Task> workList = new JList<>(workTasks);
    private final JList<Task> otherList = new JList<>(otherTasks);
    private final JList<Task> finishedList = new JList<>(finishedTasks);
    
    // Setting up main application window
    public ListifyApp2() {
        JFrame frame = new JFrame("Listify");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);
        frame.setLayout(new BorderLayout());
        
        // Header label
        JLabel header = new JLabel("Listify - Your Task Manager", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        frame.add(header, BorderLayout.NORTH);
        
        // Creating main panel with categorized task lists
        JPanel mainPanel = new JPanel(new GridLayout(1, 5));
        mainPanel.add(createTaskPanel("Personal", personalList, new Color(255, 228, 225)));
        mainPanel.add(createTaskPanel("School", schoolList, new Color(224, 255, 255)));
        mainPanel.add(createTaskPanel("Work", workList, new Color(204, 255, 204)));
        mainPanel.add(createTaskPanel("Other", otherList, new Color(255, 239, 213)));
        mainPanel.add(createTaskPanel("Finished Tasks", finishedList, new Color(220, 220, 220)));
        
        // Control panel for adding, editing, and removing tasks
        JPanel controlPanel = new JPanel(new FlowLayout());
        JTextField taskInput = new JTextField(12);
        JTextField timeInput = new JTextField(7);
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Personal", "School", "Work", "Other"});
        JButton addButton = new JButton("Add Task");
        JButton markButton = new JButton("Mark as Complete");
        JButton editButton = new JButton("Edit Task");
        JButton removeButton = new JButton("Remove Task");

        controlPanel.add(new JLabel("Input a Task"));
        controlPanel.add(taskInput);
        controlPanel.add(new JLabel("Time (HH:MM AM/PM):"));
        controlPanel.add(timeInput);
        controlPanel.add(categoryBox);
        controlPanel.add(addButton);
        controlPanel.add(markButton);
        controlPanel.add(editButton);
        controlPanel.add(removeButton);
        
        // Button actions
        addButton.addActionListener(e -> {
            String taskDescription = taskInput.getText().trim();
            String selectedCategory = (String) categoryBox.getSelectedItem();
            String timeText = timeInput.getText().trim();
            LocalTime reminderTime = null;

            if (!timeText.isEmpty()) {
                try {
                    reminderTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("hh:mm a"));
                    scheduleReminder(taskDescription, reminderTime, frame);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid time format. Use HH:MM AM/PM.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Task newTask = new Task(taskDescription, selectedCategory, reminderTime);
            tasks.add(newTask);
            getTaskListModel(selectedCategory).addElement(newTask);
            taskInput.setText("");
            timeInput.setText("");
        });

        markButton.addActionListener(e -> toggleTaskCompletion(frame));
        editButton.addActionListener(e -> editTask(frame));
        removeButton.addActionListener(e -> removeTask(frame));

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        
        // Set application icon
        ImageIcon image = new ImageIcon("C:\\Users\\RUSSEL\\Downloads\\logooo.jpg");
        frame.setIconImage(image.getImage());

    }
    
    // Schedule task reminders
    private void scheduleReminder(String taskDescription, LocalTime reminderTime, JFrame frame) {
        long delay = Duration.between(LocalTime.now(), reminderTime).toMillis();
        if (delay > 0) {
            scheduler.schedule(() -> {
                JOptionPane.showMessageDialog(frame, "Reminder: " + taskDescription, "Task Reminder", JOptionPane.INFORMATION_MESSAGE);
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
    
    // Creates a panel for a specific category
    private JPanel createTaskPanel(String title, JList<Task> list, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(bgColor);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private DefaultListModel<Task> getTaskListModel(String category) {
        return switch (category) {
            case "Personal" -> personalTasks;
            case "School" -> schoolTasks;
            case "Work" -> workTasks;
            case "Other" -> otherTasks;
            default -> new DefaultListModel<>();
        };
    }

    private void toggleTaskCompletion(JFrame frame) {
        JList<Task>[] lists = new JList[]{personalList, schoolList, workList, otherList};
        for (JList<Task> list : lists) {
            Task selectedTask = list.getSelectedValue();
            if (selectedTask != null) {
                selectedTask.toggleCompleted();
                list.repaint();
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "No task selected.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
     private void editTask(JFrame frame) {
        JList<Task>[] lists = new JList[]{personalList, schoolList, workList, otherList};
        for (JList<Task> list : lists) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1) {
                DefaultListModel<Task> model = (DefaultListModel<Task>) list.getModel();
                Task task = model.getElementAt(selectedIndex);
                String newDescription = JOptionPane.showInputDialog(frame, "Edit task description:", task.description);
                if (newDescription != null && !newDescription.trim().isEmpty()) {
                    task.description = newDescription.trim();
                    list.repaint();
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Please select a task to edit.", "Error", JOptionPane.ERROR_MESSAGE);
    }


    private void removeTask(JFrame frame) {
        JList<Task>[] lists = new JList[]{personalList, schoolList, workList, otherList};
        for (JList<Task> list : lists) {
            Task selectedTask = list.getSelectedValue();
            if (selectedTask != null) {
                getTaskListModel(selectedTask.category).removeElement(selectedTask);
                finishedTasks.addElement(selectedTask);
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "No task selected.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ListifyApp2::new);
    }
}
