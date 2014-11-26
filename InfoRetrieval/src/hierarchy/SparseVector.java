package hierarchy;

import java.util.ArrayList;

public class SparseVector {

	private final ArrayList<Integer> vector;

	/**
	 * Creates an empty SparseVector (all position are set to FALSE)
	 * with size 1.
	 */
	public SparseVector() {
		this.vector = new ArrayList<>();
	}

	public SparseVector(ArrayList<Integer> incidenceList){
		this.vector = incidenceList;
	}

	/**
	 * Creates a new SparseVector based on another (the same effect as the function copy())
	 * @param original 
	 */
	public SparseVector(SparseVector original) {
		this.vector = new ArrayList<>();
		vector.addAll(original.vector);
	}

	/**
	 * Creates a copy of this SparseVector
	 * @return a copy of this Sparse Vector.
	 */
	public SparseVector copy() {
		return new SparseVector(this);
	}

	/**
	 * 
	 * @return the number of 1s (positions set TRUE) in this vector
	 */
	public int getNum1s() {
		return vector.size();
	}
	
	public SparseVector vecReduction1(int window, int dicSize) {
		//int aggregSize = (int)Math.ceil((double)vector.size()/window); //round upny
		SparseVector aggregVec = new SparseVector();

		//por numa funcao OR
		for(int i=0; i<dicSize; i+=window) {
			int result = 0;
			for(int j=i; j<i+window && j<dicSize; j++) {
				if(vector.contains(j)) //se j estiver no vector
					result += 1;
			}
			int index = aggregVec.getVec().size();
			
			if (result > 0)
				aggregVec.add(index, 1);
			else
				aggregVec.add(index, 0);
		}
		//System.out.println(aggregVec.toString());
		//aggregVec.setSize(aggregSize);
		
		return aggregVec;
	}

	public SparseVector vecReduction(int window) {
		//int aggregSize = (int)Math.ceil((double)vector.size()/window); //round upny
		SparseVector aggregVec = new SparseVector();

		//por numa funcao OR
		for(int i=0; i<vector.size(); i+=window) {
			int result = 0;
			for(int j=i; j<i+window && j<vector.size(); j++) {
				if(vector.get(j) == 1) //se j estiver no vector
					result += 1;
			}
			int index = aggregVec.getVec().size();
			
			if (result > 0)
				aggregVec.add(index, 1);
			else
				aggregVec.add(index, 0);
		}
		//aggregVec.setSize(aggregSize);
		
		return aggregVec;
	}

	private void add(int index, int value) {
		vector.add(index, value);

	}

	@Override
	public String toString(){
		String result = "";
		for (int value : vector){
			result += "->" + value;
		}
		return result;
	}

	public ArrayList<Integer> getVec() {
		return vector;
	}

}
