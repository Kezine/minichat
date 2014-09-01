/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package destine.ecole.tools.network;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JSpinner;

public class PortInputVerifier extends InputVerifier {

    public PortInputVerifier()
    {
        super();
        System.out.println("Init");
    }
    @Override
    public boolean verify(JComponent input) {
        JSpinner js = (JSpinner)input;
        int valeur =(Integer)js.getValue();
        System.out.println("Salut");
        if(valeur >= 0 && valeur <= 65535)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
