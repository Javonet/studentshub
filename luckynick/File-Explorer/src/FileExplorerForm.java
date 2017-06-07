import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.api.INEventListener;
import com.javonet.api.NEnum;
import com.javonet.api.NObject;

import java.awt.geom.IllegalPathStateException;
import java.io.*;
import java.nio.file.FileSystem;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This class represents main window of file explorer.
 */
public class FileExplorerForm extends NObject {

    // List of files and folders in current folder
    private List<FileEntity> fileEntities = new ArrayList<>();
    // Path of current folder
    private String currDir = "C:\\";

    // Top menu with "Create" and "Delete" items.
    NObject topMenu,
            // List of files and folders from the left side of window.
            entitiesGrid,
            // TextBox with text from selected file (size limit for displayable file is 1 MB).
            fileContent;


    /**
     * Initialize and display window of file explorer.
     * @param width vertical size of window
     * @param height horizontal size of window
     * @throws JavonetException
     */
    public FileExplorerForm(int width, int height) throws JavonetException
    {
        super("System.Windows.Forms.Form");

        //Create menu for main window
        INEventListener[] handlers = {
                (params) -> CreateAction(params),
                (params) -> DeleteAction(params)
        };
        topMenu = generateMenu(new String[]{"Create", "Delete"}, handlers);
        //NObject menu = generateMenu("Create", "Read", "Update", "Delete");
        //subscribeItemsDirect(menu, handlers); //Problematic

        //Create list of files and folders (left panel)
        entitiesGrid = generateEntitiesGrid("dataGridView1", 430, 400,
                new String[]{"Type", "Name", "Size (MB)", "Modified"},
                new int[]{40, -1, 80, 180}, (params) -> CellDoubleClickAction(params));

        topMenu.invoke("SuspendLayout");
        this.invoke("SuspendLayout");

        //Create block of file content (right panel)
        fileContent = Javonet.create("System.Windows.Forms.TextBox");
        fileContent.set("Location",
                Javonet.create("System.Drawing.Point", 460, 37));
        fileContent.set("Multiline", true);
        fileContent.set("Size",
                Javonet.create("System.Drawing.Size", 411, 400));
        fileContent.set("ScrollBars", new NEnum("ScrollBars", "Vertical"));

        //Set basic parameter of main window
        setCurrDir(currDir);
        this.set("Width", width);
        this.set("Height", height);
        this.set("MaximizeBox", false);
        this.<NObject>get("Controls").invoke("Add", entitiesGrid);
        this.<NObject>get("Controls").invoke("Add", topMenu);
        this.<NObject>get("Controls").invoke("Add", fileContent);
        this.set("FormBorderStyle", new NEnum("FormBorderStyle", "FixedSingle"));
        this.set("MainMenuStrip", topMenu);

        topMenu.invoke("ResumeLayout", false);
        topMenu.invoke("PerformLayout");
        entitiesGrid.invoke("EndInit");
        this.invoke("ResumeLayout", false);
        this.invoke("PerformLayout");

        //display current dir
        fileEntities = getEntities(currDir);
        refresh();
    }

    /**
     * Set current directory path and update info in header of program.
     * @param value new directory path
     */
    private void setCurrDir(String value) throws JavonetException
    {
        this.set("Text", "File Explorer (current directory " + value + ")");
        currDir = value;
    }

    /**
     * Update information in list of files and folders depending on fileEntries list.
     * @throws JavonetException
     */
    private void refresh() throws JavonetException
    {
        entitiesGrid.<NObject>get("Rows").invoke("Clear");
        entitiesGrid.<NObject>get("Rows").invoke("Add",
                new Object[]{ new String[]{"", "<-"} } ); // "Go up" button

        for(int i = 0; i < fileEntities.size(); i++)
        {
            String size = "-", type = "dir";
            if(fileEntities.get(i).type == 'f')
            {
                size = Double.toString(fileEntities.get(i).size);
                type = "file";
            }
            //Adding elements to DataGridView
            entitiesGrid.<NObject>get("Rows").invoke("Add",
                    new Object[]{ new String[]{ type, fileEntities.get(i).name, size,
                            fileEntities.get(i).modified.toString(), Integer.toString(i) } });
        }
    }

    /**
     * Generate list of FileEntry objects (files and folders) from directory.
     * @param path points to directory which has to be analyzed
     * @return list of FileEntry objects
     */
    public List<FileEntity> getEntities(String path)
    {
        List<FileEntity> result = new ArrayList<>();
        File folder = new File(path);
        if(!folder.isDirectory()) return result; // Path must point to folder, otherwise empty list is returned.
        File[] temp = folder.listFiles();
        for(File f : temp == null ? new File[0] : temp)
        {
            FileEntity tempEntity = new FileEntity();
            tempEntity.name = f.getName();
            tempEntity.modified = new Date(f.lastModified());
            tempEntity.path = f.getPath();
            if(!f.isDirectory())
            {
                tempEntity.type = 'f';
                tempEntity.size = (double)Math.round((double)f.length()/(1024*1024)*100)/100; // File size in MB.
            }
            result.add(tempEntity);
        }

        return result;
    }

    /**
     * Create object of DataGridView C# type with additional invisible column for entities id.
     * @param name name of DataGridView object
     * @param width width of DataGridView object
     * @param height height of DataGridView object
     * @param colNames array which contains names of columns
     * @param widths array which contains horizontal lengths of columns
     * @param handler handler for double click on cell
     * @return DataGridView object
     * @throws JavonetException
     */
    public NObject generateEntitiesGrid(String name, int width, int height, String[] colNames,
                                        int[] widths, INEventListener handler) throws JavonetException
    {
        if(colNames.length != widths.length)
            throw new IllegalArgumentException("Number of columns must be equal to number of widths.");
        NObject entitiesGrid = Javonet.getType("DataGridView").create();

        //Basic settings of DataGridView
        entitiesGrid.invoke("BeginInit");
        entitiesGrid.set("AllowUserToAddRows", false);
        entitiesGrid.set("RowHeadersVisible", false);
        entitiesGrid.set("AllowUserToDeleteRows", false);
        entitiesGrid.set("ColumnHeadersHeightSizeMode",
                new NEnum("DataGridViewColumnHeadersHeightSizeMode", "AutoSize"));
        entitiesGrid.addEventListener("CellDoubleClick", handler);

        //Adding columns
        for(int i = 0; i < colNames.length; i++)
        {
            NObject col = Javonet.create("DataGridViewTextBoxColumn");
            col.set("HeaderText", colNames[i]);
            col.set("Name", colNames[i] + "Column");
            col.set("ReadOnly", true);
            col.set("SortMode", new NEnum("DataGridViewColumnSortMode",
                    "NotSortable")); // sorting causes problems with rowIndex
            if(widths[i] > -1) col.set("Width", widths[i]); //-1 means default value
            entitiesGrid.<NObject>get("Columns").invoke("Add", col);
        }

        //Final adjustments of DataGridView
        entitiesGrid.set("Location", Javonet.create("System.Drawing.Point", 12, 37));
        entitiesGrid.set("ReadOnly", true);
        entitiesGrid.set("Name", name); //!
        entitiesGrid.set("Size", Javonet.create("System.Drawing.Size", width, height));
        entitiesGrid.set("TabIndex", 1); //?

        return entitiesGrid;
    }

    /**
     * Create flat (one-dimensional) menu (MenuStrip C# type) for window with event handlers for items.
     * @param itemNames list of item names in menu
     * @param listeners handlers for click events
     * @return new menu which includes items and handlers for them
     * @throws JavonetException
     */
    private NObject generateMenu(String[] itemNames, INEventListener[] listeners) throws JavonetException
    {
        if(itemNames.length != listeners.length)
            throw new IllegalArgumentException("Number of items must be equal to number of handlers.");
        NObject menuStrip = Javonet.getType("MenuStrip").create();
        for (int i = 0; i < itemNames.length; i++) {
            NObject temp = Javonet.getType("ToolStripMenuItem").create();
            temp.set("Name", itemNames[i] + "ToolStripMenuItem");
            temp.set("Text", itemNames[i]);
            temp.set("Size", Javonet.create("System.Drawing.Size", itemNames[i].length() * 10, 20));
            temp.addEventListener("Click", listeners[i]);
            menuStrip.<NObject>get("Items").invoke("Add", temp);
        }

        return menuStrip;
    }

    /**
     * Create flat (one-dimensional) menu (MenuStrip C# type) for window
     * without event handlers for items. Would work in pair with subscribeItems() method.
     * @param itemNames list of item names in menu
     * @return new menu which includes items
     * @throws JavonetException
     */
    private NObject generateMenu(String ... itemNames) throws JavonetException
    {
        NObject menuStrip = Javonet.getType("MenuStrip").create();
        for (int i = 0; i < itemNames.length; i++) {
            NObject temp = Javonet.getType("ToolStripMenuItem").create();
            temp.set("Name", itemNames[i] + "ToolStripMenuItem");
            temp.set("Text", itemNames[i]);
            temp.set("Size", Javonet.create("System.Drawing.Size",
                    itemNames[i].length() * 10, 20));
            menuStrip.<NObject>get("Items").invoke("Add", temp);
        }

        return menuStrip;
    }


    /**
     * Theoretically this method has to assign event handlers to menu items. But it fails to execute.
     * Way of retrieving arrays from C# described in Beginners Guide doesn't work either.
     * Copy elements from ToolStripItemCollection to array of NObject objects with CopyTo method.
     * @param menu object of MenuStrip type in C#
     * @param events handlers for events produced by elements of menu
     * @throws JavonetException
     */
    @Deprecated
    private void subscribeItems(NObject menu, INEventListener ... events) throws JavonetException
    {
        NObject itemsJN = menu.get("Items");
        int num_items = itemsJN.get("Count");
        NObject items[] = new NObject[num_items];
        itemsJN.invoke("CopyTo", items, 0); //JavonetException: System.NullReferenceException:
        // Object reference not set to an instance of an object.
        for(int i = 0; i < 0; i++)
        {
            items[i].addEventListener("Click", events[i]);
        }
    }


    /**
     * Invoked when Create item from menu is clicked.
     * Create query to user to choose name of new file or folder.
     * Then create new file or folder with name specified by user and
     * refresh list of files.
     * @param params
     */
    private void CreateAction(Object[] params)
    {
        PromptForm prompt;
        try {
            prompt = new PromptForm("Provide name of new file or folder: ", "File creation");
        }
        catch (JavonetException e) {
            e.printStackTrace();
            return;
        }
        String fileName = prompt.ShowDialog();
        if(fileName == null || fileName.equals(""))
        {
            throw new IllegalStateException("Error while retrieving file name from user.");
        }
        String newPath = currDir;
        if(newPath.charAt(newPath.length() - 1) != '\\') newPath += "\\"; // Avoid concatenation of '\' to "C:\"
        newPath += fileName;
        File newFile = new File(newPath);
        if(!newFile.exists())
        {
            try {
                if(!prompt.hasFinished())
                    throw new IllegalStateException("Error while retrieving file name from user.");
                if(prompt.isFileDirectory()) newFile.mkdir();
                else newFile.createNewFile();
                fileEntities = getEntities(currDir);
                refresh();
            }
            catch (IOException | JavonetException e) {
                e.printStackTrace();
            }
        }
        else throw new IllegalPathStateException("This file or folder already exists.");
    }

    /**
     * Delete file or folder which corresponds to selected row in list of entities.
     * Select by one click on any cell in row.
     * @param params
     */
    private void DeleteAction(Object[] params)
    {
        try {
            int rowIndex = entitiesGrid.<NObject>get("CurrentCell").<Integer>get("RowIndex");
            if(rowIndex < 1) // 0 index contains back button.
            {
                System.err.println("Wrong selection.");
                return;
            }
            File target = new File(fileEntities.get(rowIndex - 1).path);
            if(target.exists())
            {
                target.delete();
                fileEntities = getEntities(currDir);
                refresh();
            }
        }
        catch (JavonetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handler for event made by double click on cell in DataGridView.
     * Handles directory and file selection, navigation to parent directory.
     * My idea was to let user select folders even in ordered list,
     * but problem with getting elements from DataGridView.Rows
     * stopped me (same problem as in subscribeItems() method).
     * I can't really find a way to obtain elements of C# collections.
     * @param params parameters of new event
     */
    private void CellDoubleClickAction(Object[] params)
    {
        NObject args = (NObject) params[1];
        try {
            int rowIndex = args.<Integer>get("RowIndex");
            if(rowIndex == 0)
                setCurrDir(new File(currDir).getParent()); // navigate to parent folder
            else if(fileEntities.get(rowIndex - 1).type == 'd')
                setCurrDir(fileEntities.get(rowIndex - 1).path); // nav to selected folder
            else if(fileEntities.get(rowIndex - 1).type == 'f') /* display text from file */
            {
                String content = "";
                if(fileEntities.get(rowIndex - 1).size > 1)
                    content = "* Sorry, we can't display so big file *";
                else
                {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(fileEntities.get(rowIndex - 1).path));
                        while(br.ready())
                        {
                            content += br.readLine() + "\r\n";
                        }
                    }
                    catch (FileNotFoundException e) {
                        System.err.println("File not found.");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                fileContent.set("Text", content);
                return;
            }
            fileEntities = getEntities(currDir);
            refresh();
        }
        catch (JavonetException e) {
            e.printStackTrace();
        }
    }
}
