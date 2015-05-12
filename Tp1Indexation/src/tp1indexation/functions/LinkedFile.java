/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation.functions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalei
 */
public class LinkedFile {

    protected String name;
    protected int occurence = 0;
    protected List<Integer> positions = new ArrayList<>();
    protected int nbTermes;
    protected String wordCommingFrom;

    public LinkedFile(String path, int nbTermes, int position,String word) {
        this.name=path;
        this.positions.add(position);
        this.nbTermes=nbTermes;
        this.wordCommingFrom=word;
        occurence++;
    }

    public int getOccurence() {
        return occurence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void addPosition(int position) {
        this.positions.add(position);
    }
    
    public double getFrequency(){
        return (double) occurence/(double) nbTermes;
    }
}
