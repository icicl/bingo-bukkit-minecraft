import org.bukkit.Material;

import java.io.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class MaterialDisplay {
    public MaterialDisplay(){
        try {
            loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Dictionary MATERIALS_DISPLAY=new Hashtable();

    private void loadFromFile() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("resources/displaydata.frost");
        String itemName = "";
        int tmp;
        int ltmp;
        byte[] palette = new byte[4];
        byte[][] display;
        int index;
        while (true) {
            display = new byte[16][16];
            index = 0;
            tmp = is.read();
            if (tmp == -1) {
                break;
            }
            itemName="";
            for (; tmp > 0; tmp--) {
                itemName += (char) is.read();
            }
            for (tmp = 0; tmp < 4; tmp++) {
                palette[tmp] = (byte) is.read();
            }
            ltmp = is.read() + 1;
            while (index < 256) {
                tmp = is.read();
                if (tmp < 4) {
                    for (int i = is.read() + 1; i > 0; i--) {
                        display[index >> 4][index & 15] = palette[tmp];
                        index++;
                    }
                } else {
                    display[index >> 4][index & 15] = (byte) tmp;
                    index++;
                }
            }
            MATERIALS_DISPLAY.put(Material.getMaterial(itemName.toUpperCase()), display);
        }
    }

    public Dictionary get(){
        return MATERIALS_DISPLAY;
    }
}
