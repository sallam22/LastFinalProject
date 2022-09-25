package controller;

import model.FileOperations;
import model.InvoiceHeader;
import model.InvoiceLine;
import view.InvoiceFrame;
import view.NewInvoiceDialog;
import view.NewItemDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActionHandler implements ActionListener {

    // == Fields ==
    NewInvoiceDialog invoiceDialog;
    NewItemDialog itemDialog;

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {

            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
           case "Create Item":
                saveItem();
                break;
             case "Delete Item":
                cancelItem();
                break;
        }

    }


    private void loadFile() {

        String invoicesFilePath = null;
        String itemsFilePath = null;

        JFileChooser fc1 = new JFileChooser();
        if (fc1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            invoicesFilePath = fc1.getSelectedFile().getPath();
            boolean suffix = invoicesFilePath.endsWith("csv");
            if (!suffix) {
                System.out.println("Files must be CSV file only");
                return;
            }
            System.out.println("Invoices CSV File is selected");
            System.out.println("******************************************");
        }

        JFileChooser fc2 = new JFileChooser();
        if (fc2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            itemsFilePath = fc2.getSelectedFile().getPath();
            boolean suffix = itemsFilePath.endsWith("csv");
            if (!suffix) {
                System.out.println("Files must be CSV file only");
                return;
            }
            System.out.println("Invoice's items CSV file is selected");
            System.out.println("******************************************");
        }

        if (invoicesFilePath != null && itemsFilePath != null) {
            InvoiceFrame.invoices = FileOperations.readFile(invoicesFilePath, itemsFilePath);
            Object[][] table1Data = getInvoiceTableData(InvoiceFrame.invoices);
            InvoiceFrame.invoicesTable.setModel(new DefaultTableModel(table1Data,
                    new String[]{"No.", "Date", "Customer", "Total"}));

            for (InvoiceHeader invoice : InvoiceFrame.invoices) {
                System.out.println(invoice);
                System.out.println("*********************************************");
            }
        } else {
            System.out.println("You must select two CSV files");
            System.out.println("*************************************************");
        }

    }

    private void saveFile() {

        String invoicesFilePath = null;
        String itemsFilePath = null;

        JFileChooser fc1 = new JFileChooser();
        if (fc1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            invoicesFilePath = fc1.getSelectedFile().getPath();
            boolean suffix = invoicesFilePath.endsWith("csv");
            if (!suffix) {
                System.out.println("Files must be CSV file only");
                return;
            }
            System.out.println("Invoices File is selected");
            System.out.println("******************************************");
        }

        JFileChooser fc2 = new JFileChooser();
        if (fc2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            itemsFilePath = fc2.getSelectedFile().getPath();
            boolean suffix = itemsFilePath.endsWith("csv");
            if (!suffix) {
                System.out.println("Files must be CSV file only");
                return;
            }
            System.out.println("Invoice's items file is selected");
            System.out.println("******************************************");
        }

        if (invoicesFilePath != null && itemsFilePath != null) {
            FileOperations.writeFile(InvoiceFrame.invoices, invoicesFilePath, itemsFilePath);

        } else {
            System.out.println("You must select two files");
            System.out.println("*************************************************");
        }
    }

    private void createNewInvoice() {

        invoiceDialog = new NewInvoiceDialog(null, true);
        invoiceDialog.setVisible(true);

        int invoiceNum = InvoiceFrame.invoices.size() + 1;
        String dateString = invoiceDialog.getInvoiceDate();
        String customerName = invoiceDialog.getCustomerName();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (dateString != null && !(customerName.isEmpty())) {
                Date invoiceDate = df.parse(dateString);
                InvoiceHeader invoice = new InvoiceHeader(invoiceNum, invoiceDate, customerName);
                InvoiceFrame.invoices.add(invoice);
                Object[][] invoiceTableData = getInvoiceTableData(InvoiceFrame.invoices);
                InvoiceFrame.invoicesTable.setModel(new DefaultTableModel(invoiceTableData,
                        new String[]{"No.", "Date", "Customer", "Total"}));
            } else {
                System.out.println("You must insert date and customer name");
            }

        } catch (ParseException ex) {
            System.out.println("Incorrecet Date Format ====> It need to be (dd-MM-yyyy) ");
            System.out.println("***********************************************************");
        }

    }

    private void deleteInvoice() {
        if (InvoiceFrame.invoicesTable.getSelectedRow() >= 0) {
            InvoiceFrame.invoices.remove(InvoiceFrame.invoicesTable.getSelectedRow());
            Object[][] invoiceTableData = getInvoiceTableData(InvoiceFrame.invoices);
            InvoiceFrame.invoicesTable.setModel(new DefaultTableModel(invoiceTableData,
                    new String[]{"No.", "Date", "Customer", "Total"}));
            InvoiceFrame.invoiceNumLbl.setText("0");
            InvoiceFrame.invoiceDateTxtField.setText("");
            InvoiceFrame.customerNameTxtField.setText("");
            InvoiceFrame.invoiceTotalLbl.setText("0.0");
            InvoiceFrame.itemsTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{
                            {null, null, null, null, null},
                            {null, null, null, null, null},
                            {null, null, null, null, null},
                            {null, null, null, null, null},
                            {null, null, null, null, null}
                    },
                    new String[]{
                            "No.", "Item Name", "Item Price", "Count", "Item Total"
                    }
            ));
        } else {
            System.out.println("Select Invoice First");
            System.out.println("*****************************");
        }
    }

    private void saveItem() {

        itemDialog = new NewItemDialog(null, true);
        itemDialog.setVisible(true);

        int selectedRow = InvoiceFrame.invoicesTable.getSelectedRow();
        if (selectedRow >= 0) {
            InvoiceHeader invoice = InvoiceFrame.invoices.get(selectedRow);
            ArrayList<InvoiceLine> invoiceItems = invoice.getInvoiceLines();
            if (invoiceItems == null) {
                invoiceItems = new ArrayList<>();
                invoice.setInvoiceLines(invoiceItems);
            }
            InvoiceLine item = new InvoiceLine(invoice, itemDialog.getItemName(),
                    itemDialog.getItemPrice(), itemDialog.getCount());
            if (!((itemDialog.getName()).isEmpty()) && itemDialog.getItemPrice() > 0.0 && itemDialog.getCount() > 0) {
                invoiceItems.add(item);
            }

            double total = 0.0;
            for (InvoiceLine invoiceItem : invoiceItems) {
                total += invoiceItem.getItemPrice() * invoiceItem.getCount();
            }

            InvoiceFrame.invoiceTotalLbl.setText(String.valueOf(total));

            Object[][] table2Data = getInvoiceItemsTableData(invoiceItems);
            InvoiceFrame.itemsTable.setModel(new DefaultTableModel(table2Data,
                    new String[]{"No.", "Item Name", "Item Price", "Count", "Item Total"}));

            Object[][] table1Data = getInvoiceTableData(InvoiceFrame.invoices);
            InvoiceFrame.invoicesTable.setModel(new DefaultTableModel(table1Data,
                    new String[]{"No.", "Date", "Customer", "Total"}));
        } else {
            System.out.println("Select Invoice First");
            System.out.println("******************************");
        }
    }

    private void cancelItem() {
        int selectedRowInInvoiceTable = InvoiceFrame.invoicesTable.getSelectedRow();
        if (selectedRowInInvoiceTable >= 0) {

            InvoiceHeader invoice = InvoiceFrame.invoices.get(selectedRowInInvoiceTable);
            ArrayList<InvoiceLine> items = invoice.getInvoiceLines();
            if (items == null) {
                items = new ArrayList<>();
                invoice.setInvoiceLines(items);
            }
            int selectedRowInItemsTable = InvoiceFrame.itemsTable.getSelectedRow();
            if (selectedRowInItemsTable >= 0) {
                items.remove(selectedRowInItemsTable);
                double total = 0.0;
                for (InvoiceLine invoiceItem : items) {
                    total += invoiceItem.getItemPrice() * invoiceItem.getCount();
                }

                InvoiceFrame.invoiceTotalLbl.setText(String.valueOf(total));

                Object[][] table2Data = getInvoiceItemsTableData(items);
                InvoiceFrame.itemsTable.setModel(new DefaultTableModel(table2Data,
                        new String[]{"No.", "Item Name", "Item Price", "Count", "Item Total"}));

                Object[][] table1Data = getInvoiceTableData(InvoiceFrame.invoices);
                InvoiceFrame.invoicesTable.setModel(new DefaultTableModel(table1Data,
                        new String[]{"No.", "Date", "Customer", "Total"}));

            } else {
                System.out.println("Select Invoice and Item in the same time");
                System.out.println("******************************************");
            }
        } else {
            System.out.println("Select Invoice and Item in the same time");
            System.out.println("*********************************************");
        }
    }


    // Helper Methods
    private Object[][] getInvoiceTableData(ArrayList<InvoiceHeader> invoices) {

        Object[][] tableData = new Object[invoices.size()][4];
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (int i = 0; i < invoices.size(); i++) {
            tableData[i][0] = invoices.get(i).getInvoiceNum();
            tableData[i][1] = sdf.format(invoices.get(i).getInvoiceDate());
            tableData[i][2] = invoices.get(i).getCustomerName();
            double total = 0.0;
            if (invoices.get(i).getInvoiceLines() != null) {
                for (InvoiceLine item : invoices.get(i).getInvoiceLines()) {
                    total += item.getItemPrice() * item.getCount();
                }
            }

            tableData[i][3] = total;

        }

        return tableData;
    }

    private Object[][] getInvoiceItemsTableData(ArrayList<InvoiceLine> items) {

        Object[][] tableData = new Object[items.size()][5];
        for (int i = 0; i < items.size(); i++) {
            tableData[i][0] = items.get(i).getInvoice().getInvoiceNum();
            tableData[i][1] = items.get(i).getItemName();
            tableData[i][2] = items.get(i).getItemPrice();
            tableData[i][3] = items.get(i).getCount();
            double itemTotal = items.get(i).getItemPrice() * items.get(i).getCount();
            tableData[i][4] = itemTotal;

        }

        return tableData;

    }

}
