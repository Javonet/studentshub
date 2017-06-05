import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    /**
     * Entry point of program.
     * @param args
     */
    public static void main(String ... args)
    {
        try
        {
            // In order to run this program, you need to place activation file to
            // root project directory. This file must contain activation e-mail
            // in first line and licence key in second line.
            String activationFile = "activation.txt";
            BufferedReader bf = new BufferedReader(new FileReader(activationFile));
            String email = null, key = null;
            try {
                email = bf.readLine();
                key = bf.readLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if(email == null || key == null)
                throw new IllegalStateException("File has wrong structure.");
            Javonet.activate(email, key, JavonetFramework.v40);
        }
        catch (JavonetException e)
        {
            System.err.println("Internet or activation problems.");
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("Put activation file in project directory!");
        }

        try
        {
            // Add appropriate libraries
            Javonet.addReference("System.Windows.Forms");
            Javonet.addReference("System.Drawing");
        }
        catch (JavonetException e)
        {
            System.out.println(".NET environment is not installed on this machine.");
        }

        try
        {
            // Initialize environment
            NType applicationType = Javonet.getType("Application");
            applicationType.invoke("EnableVisualStyles");
            applicationType.invoke("SetCompatibleTextRenderingDefault", false);

            // Display window
            FileExplorerForm mainWindow = new FileExplorerForm(900, 500);
            applicationType.invoke("Run", mainWindow);
        }
        catch (JavonetException e)
        {
            e.printStackTrace();
        }

    }

}
