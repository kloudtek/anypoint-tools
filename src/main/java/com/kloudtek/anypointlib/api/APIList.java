package com.kloudtek.anypointlib.api;

import com.kloudtek.anypointlib.AnypointObject;
import com.kloudtek.anypointlib.Organization;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class APIList extends AnypointObject<Organization> implements Iterable<API> {
    private final int limit;
    private int total;
    private final String nameFilter;
    private int offset;
    private boolean allLoaded;
    private List<API> apis;

    public APIList(Organization organization, String nameFilter, int offset, int limit, int total, List<API> apis) {
        super(organization);
        this.nameFilter = nameFilter;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.apis = apis;
        allLoaded = offset + apis.size() > total;
        for (API api : apis) {
            api.setParent(organization);
        }
    }

    public int getTotal() {
        return total;
    }

    public int getLoadedSize() {
        return apis.size();
    }

    /**
     * Indicates if all available APIs (excluding those prior to offset) are loaded in this list. If they aren't iterating will automating retrieve the remaining ones
     *
     * @return true if all APIs are loaded, false otherwise
     */
    public boolean isAllLoaded() {
        return allLoaded;
    }

    @NotNull
    @Override
    public Iterator<API> iterator() {
        return new APIIterator(apis.iterator());
    }

    @Override
    public void forEach(Consumer<? super API> action) {
        throw new RuntimeException("Not supported yet, sorry :(");
    }

    @Override
    public Spliterator<API> spliterator() {
        throw new RuntimeException("Not supported yet, sorry :(");
    }

    class APIIterator implements Iterator<API> {
        private Iterator<API> it;

        public APIIterator(Iterator<API> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            try {
                if (!it.hasNext() && !allLoaded) {
                    offset += limit;
                    APIList moreApis = parent.getAPIs(nameFilter, offset, limit);
                    apis = moreApis.apis;
                    it = apis.iterator();
                }
                return it.hasNext();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public API next() {
            return it.next();
        }
    }
}
