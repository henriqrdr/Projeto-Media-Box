package mediabox.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class ItemChangeListener implements ItemListener{
    @Override
    public void itemStateChanged(ItemEvent event) {
       if (event.getStateChange() == ItemEvent.SELECTED) {
          Object item = event.getItem();
          System.out.println(item.toString());
       }
    }

	
}
