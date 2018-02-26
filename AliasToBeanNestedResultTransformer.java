/**
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.hibernate.transform.ResultTransformer;

/**
 * @author samiandoni
 *
 */
public class AliasToBeanNestedResultTransformer extends AliasedTupleSubsetResultTransformer {

	private static final long serialVersionUID = -8047276133980128266L;
	
	private final Class<?> resultClass;

	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}
	
	public AliasToBeanNestedResultTransformer(Class<?> resultClass) {
		
		this.resultClass = resultClass;
	}
	
	@SuppressWarnings("unchecked")
	public Object transformTuple(Object[] tuple, String[] aliases) {
		
		Map<Class<?>, List<?>> subclassToAlias = new HashMap<Class<?>, List<?>>();
		List<String> nestedAliases = new ArrayList<String>();
		
		try {
			for (int i = 0; i < aliases.length; i++) {
				String alias = aliases[i];
				if (alias.contains(".")) {
					nestedAliases.add(alias);
					
					String[] sp = alias.split("\\.");
					String fieldName = sp[0];
					String aliasName = sp[1];
					
					Class<?> subclass = resultClass.getDeclaredField(fieldName).getType();
					
					if (!subclassToAlias.containsKey(subclass)) {
						List<Object> list = new ArrayList<Object>();
						list.add(new ArrayList<Object>());
						list.add(new ArrayList<String>());
						list.add(fieldName);
						subclassToAlias.put(subclass, list);
					}
					((List<Object>)subclassToAlias.get(subclass).get(0)).add(tuple[i]);
					((List<String>)subclassToAlias.get(subclass).get(1)).add(aliasName);
				}
			}
		}
		catch (NoSuchFieldException e) {
			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
		}
		
		Object[] newTuple = new Object[aliases.length - nestedAliases.size()];
		String[] newAliases = new String[aliases.length - nestedAliases.size()];
		int i = 0;
		for (int j = 0; j < aliases.length; j++) {
			if (!nestedAliases.contains(aliases[j])) {
				newTuple[i] = tuple[j];
				newAliases[i] = aliases[j];
				++i;
			}
		}
		
		ResultTransformer rootTransformer = new AliasToBeanResultTransformer(resultClass);
		Object root = rootTransformer.transformTuple(newTuple, newAliases);
		
		for (Class<?> subclass : subclassToAlias.keySet()) {
			ResultTransformer subclassTransformer = new AliasToBeanResultTransformer(subclass);
			Object subObject = subclassTransformer.transformTuple(
					((List<Object>)subclassToAlias.get(subclass).get(0)).toArray(), 
					((List<Object>)subclassToAlias.get(subclass).get(1)).toArray(new String[0])
					);
			PropertyAccessStrategy accessor = PropertyAccessStrategyFieldImpl.INSTANCE;
			PropertyAccess access = accessor.buildPropertyAccess(resultClass, (String) subclassToAlias.get(subclass).get(2));
			access.getSetter().set(root, subObject, null);
		}
		
		return root;
	}
}
