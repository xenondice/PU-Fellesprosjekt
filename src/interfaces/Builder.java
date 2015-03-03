package interfaces;

/**
 * A Builder is a class that can incrementally build another class.
 * This other class is mostly supposed to be immutable.
 *
 * @param <E> The class which the builder is building.
 */
public interface Builder<E> {
	
	/**
	 * This method creates the (immutable) instance out of the current state of the builder.
	 * @return an instance of the class being built.
	 */
	public E build();
	
}
