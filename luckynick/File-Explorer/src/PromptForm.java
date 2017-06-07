import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.api.NEnum;
import com.javonet.api.NObject;

public class PromptForm extends NObject{

    private NObject textBox, checkBox;
    // Set after query is done.
    private boolean isDir = false, isDone = false;

    /**
     * Create new query form and initialize it.
     * @param text title of query
     * @param caption title of window
     * @throws JavonetException
     */
    protected PromptForm(String text, String caption) throws JavonetException {
        super("System.Windows.Forms.Form");
        // Basic setup of object.
        this.set("Width", 500);
        this.set("Height", 150);
        this.set("FormBorderStyle", new NEnum("FormBorderStyle", "FixedDialog"));
        this.set("Text", caption);

        // Title of query.
        NObject label = Javonet.create("System.Windows.Forms.Label");
        label.<NObject>set("Text", text).<NObject>set("Width", 300).<NObject>set("Left", 50).<NObject>set("Top", 20);

        // User's input.
        textBox = Javonet.create("System.Windows.Forms.TextBox");
        textBox.<NObject>set("Width", 400).<NObject>set("Left", 50).<NObject>set("Top", 50);

        // Confirmation button.
        NObject confirm = Javonet.create("System.Windows.Forms.Button");
        confirm.<NObject>set("Text", "OK").<NObject>set("Left", 50).<NObject>set("Top", 70)
                .<NObject>set("Width", 40)
                .<NObject>set("DialogResult", new NEnum("DialogResult", "OK"));
        confirm.addEventListener("Click", (args) -> {
            try {
                this.invoke("Close");
            }
            catch (JavonetException e) {
                e.printStackTrace();
            }
        });

        // Check if creating directory.
        checkBox = Javonet.create("System.Windows.Forms.CheckBox");
        checkBox.set("Text", "Directory");

        // Adding elements.
        this.<NObject>get("Controls").invoke("Add", label);
        this.<NObject>get("Controls").invoke("Add", textBox);
        this.<NObject>get("Controls").invoke("Add", confirm);
        this.<NObject>get("Controls").invoke("Add", checkBox);
        this.set("AcceptButton", confirm);
    }

    /**
     * Display form represented by this object.
     * Get input from user.
     * @return String from TextBox
     */
    public String ShowDialog()
    {
        try {
            NEnum result = this.<NEnum>invoke("ShowDialog");
            if("OK".equals(result.getValueName()))
            {
                isDone = true; // Query is done
                isDir = checkBox.<Boolean>get("Checked");
                return textBox.<String>get("Text");
            }
        }
        catch (JavonetException e) {
            System.err.println("Query form is not configured properly.");
        }
        return null;
    }

    /**
     *
     * @return true if query was finished
     */
    public  boolean hasFinished()
    {
        return isDone;
    }

    /**
     *
     * @return true if user has chosen to create directory
     */
    public boolean isFileDirectory()
    {
        return isDone && isDir;
    }
}
