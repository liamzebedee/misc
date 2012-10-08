/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessur.engine.lighting;

import java.util.ArrayList;

public class LoopingList<E> extends ArrayList<E> {

    public LoopingList() {
        super();
    }

    @Override
    public E get(int index) {
        if(size()==1) index=0;
        else if(index < 0 || index >= size()) index = index % size();
        if(index<0) index = size() + index;
        return super.get(index);
    }


}
