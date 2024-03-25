package com.example.lab3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class WindowController implements Initializable {
    public Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBusesFromFile();
        setTable(buses);
        loadComboBoxData(buses);
    }
    @FXML
    private AnchorPane Program;
    @FXML
    private AnchorPane SignUp;
    private boolean isAdmin = false;
    @FXML
    private Label label;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox fromField;
    @FXML
    private ComboBox whereField;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button signupButton;
    @FXML
    private Button changeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    private final List<Bus> buses = new ArrayList<>();
    private String loggedInUser;
    private boolean isHistory = false;
    @FXML
    private TableView<Bus> bookTableView;
    @FXML
    private TableColumn<Bus, String> number;
    @FXML
    private TableColumn<Bus, String> from;
    @FXML
    private TableColumn<Bus, String> where;
    @FXML
    private TableColumn<Bus, String> firsttime;
    @FXML
    private TableColumn<Bus, String> secondtime;
    public void logIn(){
        String login = loginField.getText();
        String password = passwordField.getText();
        try {
            File file = new File("users.txt");
            Scanner scanner = new Scanner(file);

            // Читаємо файл рядок за рядком
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.equals(login + ";" + password)) {
                    SignUp.setVisible(false);
                    SignUp.setDisable(true);
                    Program.setVisible(true);
                    Program.setDisable(false);
                    loggedInUser = login;

                    setTable(buses);

                    if (line.equals("admin;admin")){
                        isAdmin = true;
                        saveButton.setVisible(true);
                        addButton.setVisible(true);
                        deleteButton.setVisible(true);
                        changeButton.setVisible(true);
                        System.out.println("Режим адміна");
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    public void signUp() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (userExists(login)) {
            label.setText("Користувач з таким логіном вже існує!");
            return;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
            writer.write(login + ";" + password);
            writer.newLine();
            writer.close();
            label.setText("Вітаю! Реєстрація успішна! \nНатисніть кнопку 'Увійти', щоб продовжити роботу");
            signupButton.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean userExists(String login) {
        try {
            File file = new File("users.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");

                if (parts.length == 2) {
                    String existingLogin = parts[0];
                    if (existingLogin.equals(login)) {
                        scanner.close();
                        return true;
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void setTable(List<Bus> buses) {
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        from.setCellValueFactory(new PropertyValueFactory<>("from"));
        where.setCellValueFactory(new PropertyValueFactory<>("where"));
        firsttime.setCellValueFactory(new PropertyValueFactory<>("firsttime"));
        secondtime.setCellValueFactory(new PropertyValueFactory<>("secondtime"));
        ObservableList<Bus> busList = FXCollections.observableArrayList(buses);

        busList.sort(Comparator.comparing(Bus::getNumber));
        bookTableView.setItems(busList);

    }
    public void searchBuses() {
        String from = fromField.getValue() != null ? fromField.getValue().toString() : "";
        String where = whereField.getValue() != null ? whereField.getValue().toString() : "";
        String date = dateField.getValue() != null ? dateField.getValue().toString() : "";

        List<Bus> searchResults = new ArrayList<>();

        for (Bus bus : buses) {
            String busFrom = bus.getFrom();
            String busWhere = bus.getWhere();
            String busFirstTime = bus.getFirsttime();
            String busSecondTime = bus.getSecondtime();

            boolean matchFrom = from.isEmpty() || busFrom.equals(from);
            boolean matchWhere = where.isEmpty() || busWhere.equals(where);
            boolean matchDate = date.isEmpty() || (busFirstTime.contains(date) || busSecondTime.contains(date));

            if (matchFrom && matchWhere && matchDate) {
                searchResults.add(bus);
            }
        }
        setTable(searchResults);
    }

    public void deleteBus() {
        if (isAdmin) {
            Bus selectedBus = bookTableView.getSelectionModel().getSelectedItem();

            if (selectedBus != null) {
                buses.remove(selectedBus);

                setTable(buses);
                loadComboBoxData(buses);
            }
        }
    }
    private void loadComboBoxData(List<Bus> buses) {
        Set<String> fromSet = new HashSet<>();
        Set<String> whereSet = new HashSet<>();

        for (Bus bus : buses) {
            fromSet.add(bus.getFrom());
            whereSet.add(bus.getWhere());
        }

        fromField.setItems(FXCollections.observableArrayList(fromSet));
        whereField.setItems(FXCollections.observableArrayList(whereSet));
    }
    public void addBus() {
        if (isAdmin) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add New Bus");
            dialog.setHeaderText(null);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField numberField = new TextField();
            TextField fromField = new TextField();
            TextField whereField = new TextField();
            TextField firstField = new TextField();
            TextField secondField = new TextField();
            DatePicker date2Field = new DatePicker();

            setGrid(grid, numberField, fromField, whereField, firstField, secondField, date2Field);

            dialog.getDialogPane().setContent(grid);
            Optional<ButtonType> result = dialog.showAndWait();

            result.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    int number = Integer.parseInt(numberField.getText());
                    String from = fromField.getText();
                    String where = whereField.getText();
                    String firsttime = firstField.getText();
                    String secondtime = secondField.getText();
                    String date = date2Field.getValue().toString();

                    Bus newBus = new Bus(number, from, where, firsttime, secondtime, date);

                    buses.add(newBus);

                    setTable(buses);
                    loadComboBoxData(buses);
                }
            });
        }
    }
    private void setGrid(GridPane grid, TextField numberField, TextField fromField,
                         TextField whereField, TextField firstField, TextField secondField, DatePicker date2Field){
        grid.add(new Label("Number:"), 0, 0);
        grid.add(numberField, 1, 0);

        grid.add(new Label("From:"), 0, 1);
        grid.add(fromField, 1, 1);

        grid.add(new Label("Where:"), 0, 2);
        grid.add(whereField, 1, 2);

        grid.add(new Label("FirstTime:"), 0, 3);
        grid.add(firstField, 1, 3);

        grid.add(new Label("SecondTime:"), 0, 4);
        grid.add(secondField, 1, 4);

        grid.add(new Label("Date:"), 0, 5);
        grid.add(date2Field, 1, 5);
    }
    public void buyBus() {
        if (loggedInUser != null) {
            Bus selectedBus = bookTableView.getSelectionModel().getSelectedItem();

            if (selectedBus != null) {
                String purchase = loggedInUser + ";" + selectedBus.getNumber() + ";" + selectedBus.getFrom() + ";" +
                        selectedBus.getWhere() + ";" + selectedBus.getFirsttime() + ";" +
                        selectedBus.getSecondtime() + ";" + selectedBus.getDate();

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt", true));
                    writer.write(purchase);
                    writer.newLine();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void busHistory() {
        isHistory = !isHistory;
        if (isHistory) {
            try {
                File file = new File("history.txt");
                Scanner scanner = new Scanner(file);

                List<Bus> buses = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    String[] parts = line.split(";");

                    if (parts.length == 7) {
                        String login = parts[0];
                        int number = Integer.parseInt(parts[1]);
                        String from = parts[2];
                        String where = parts[3];
                        String firsttime = parts[4];
                        String secondtime = parts[5];
                        String date = parts[6];
                        if (login.equals(loggedInUser)) {
                            Bus bus = new Bus(number, from, where, firsttime, secondtime, date);
                            buses.add(bus);
                        }
                    }
                }

                scanner.close();

                setTable(buses);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            setTable(buses);
        }
    }
    public void changeBus() {
        if (isAdmin) {
            Bus selectedBus = bookTableView.getSelectionModel().getSelectedItem();

            if (selectedBus != null) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Change Bus Details");
                dialog.setHeaderText(null);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);

                TextField numberField = new TextField(String.valueOf(selectedBus.getNumber()));
                TextField fromField = new TextField(selectedBus.getFrom());
                TextField whereField = new TextField(selectedBus.getWhere());
                TextField firstField = new TextField(selectedBus.getFirsttime());
                TextField secondField = new TextField(selectedBus.getSecondtime());
                DatePicker date2Field = new DatePicker(LocalDate.parse(selectedBus.getDate()));

                setGrid(grid, numberField, fromField, whereField, firstField, secondField, date2Field);

                dialog.getDialogPane().setContent(grid);

                Optional<ButtonType> result = dialog.showAndWait();

                result.ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {

                        selectedBus.setNumber(Integer.parseInt(numberField.getText()));
                        selectedBus.setFrom(fromField.getText());
                        selectedBus.setWhere(whereField.getText());
                        selectedBus.setFirsttime(firstField.getText());
                        selectedBus.setSecondtime(secondField.getText());
                        selectedBus.setDate(date2Field.getValue().toString());

                        setTable(buses);
                        loadComboBoxData(buses);
                    }
                });
            }
        }
    }
    private void loadBusesFromFile() {
        try {
            File file = new File("buses.txt");
            Scanner scanner = new Scanner(file);

            List<Bus> loadedBuses = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");

                if (parts.length == 6) {
                    int number = Integer.parseInt(parts[0]);
                    String from = parts[1];
                    String where = parts[2];
                    String firsttime = parts[3];
                    String secondtime = parts[4];
                    String date = parts[5];

                    Bus bus = new Bus(number, from, where, firsttime, secondtime, date);
                    loadedBuses.add(bus);
                }
            }

            scanner.close();
            buses.addAll(loadedBuses);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveBusListToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("buses.txt"));
            for (Bus bus : buses) {
                String line = bus.getNumber() + ";" + bus.getFrom() + ";" + bus.getWhere() + ";" +
                        bus.getFirsttime() + ";" + bus.getSecondtime() + ";" + bus.getDate();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
