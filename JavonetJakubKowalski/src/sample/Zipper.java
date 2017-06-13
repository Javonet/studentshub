package sample;

import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NType;


/**
 * Created by Jakub Kowalski on 11.06.2017.
 */
public class Zipper {

    //String extractPath = "C:\\Users\\Jakub Kowalski\\IdeaProjects\\JavonetApp\\extract1234";

    public void zipIt(String a, String b, String c) throws JavonetException {
        try {
            com.javonet.Javonet.activate("kubabartek1@op.pl", "o8AK-Rz4n-Me67-z3G9-Wn46", JavonetFramework.v40);
            com.javonet.Javonet.addReference("System.Windows.Forms");
            com.javonet.Javonet.addReference("System.IO.Compression");
            com.javonet.Javonet.addReference("System.IO.Compression.FileSystem.dll");

            //com.javonet.Javonet.getType("MessageBox").invoke("Show", "Hello from .NET!");

            String start = a;
            String zipPath = c + "\\" + b + ".zip";

            NType zip = com.javonet.Javonet.getType("ZipFile");
            zip.invoke("CreateFromDirectory", start, zipPath);
            //zip.invoke("ExtractToDirectory", zipPath, extractPath);

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
