package de.unima.ki.narminer.utils;

public abstract class IDResolver {
    private static IDResolver resolver = new DummyResolver();

    public static void setResolver(IDResolver resolver) {
        IDResolver.resolver = resolver;
    }

    public static IDResolver getResolver() {
        return resolver;
    }

    public static class DummyResolver extends IDResolver {
        @Override
        public String resolve(int id) {
            return Integer.toString(id);
        }
    }

    public static class StaticResolver extends IDResolver {
        @Override
        public String resolve(int id) {
            return Character.toString(((char) ('A' + id)));
        }
    }


    /**
     * Returns the text name of the given ID
     *
     * @param id id to lookup name for
     * @return name for given id
     */
    public abstract String resolve(int id);


}
