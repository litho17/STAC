package com.cyberpointllc.stac.snapbuddy.handler;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.cyberpointllc.stac.hashmap.HashMap;
import com.cyberpointllc.stac.snapservice.SnapContext;
import com.cyberpointllc.stac.snapservice.SnapService;
import com.cyberpointllc.stac.snapservice.model.Filter;
import com.cyberpointllc.stac.snapservice.model.FilterFactory;
import com.cyberpointllc.stac.snapservice.model.Photo;
import com.cyberpointllc.stac.snapservice.model.Person;
import com.cyberpointllc.stac.template.TemplateEngine;
import com.cyberpointllc.stac.webserver.handler.HttpHandlerResponse;
import com.cyberpointllc.stac.webserver.handler.MultipartHelper;
import com.sun.net.httpserver.HttpExchange;
import java.util.Random;

public class FilterHandler extends AbstractTemplateSnapBuddyHandler {

    private final String redirectResponsePath;

    private static final Collection<Filter> filters = FilterFactory.getFilters();

    private static final String PATH = "/filter/";

    private static final String TITLE = "Apply Filters";

    private static final String FIELD_NAME = "filter list";

    private static final TemplateEngine IMAGE_TEMPLATE = new  TemplateEngine("    <center>" + "        <div class=\"photos\">" + "            <img src=\"{{photoURL}}\" alt=\"{{photoCaption}}\" /><br/>" + "            {{photoCaption}}" + "        </div>" + "    </center>");

    public FilterHandler(SnapService snapService, String redirectResponsePath) {
        super(snapService);
        this.redirectResponsePath = redirectResponsePath;
    }

    @Override
    protected String getTitle(SnapContext context) {
        return TITLE;
    }

    @Override
    protected String getContents(SnapContext context) {
        ClassgetContents replacementClass = new  ClassgetContents(context);
        ;
        replacementClass.doIt0();
        replacementClass.doIt1();
        replacementClass.doIt2();
        replacementClass.doIt3();
        return replacementClass.doIt4();
    }

    @Override
    public String getPath() {
        return PATH;
    }

    /*
     * Creates a drop-down list of all filters, with the given filter selected.
     * If null is given, "No Filter" is selected
     */
    private String createDropDownBox(Filter appliedFilter) {
        StringBuilder sb = new  StringBuilder();
        sb.append("<select name=\"");
        sb.append(FIELD_NAME);
        sb.append("\">");
        // make an option for No Filter
        sb.append("<option value=\"No Filter\" ");
        // decide if this should be the default option
        if (appliedFilter == null) {
            sb.append("selected");
        }
        sb.append(">No Filter</option>");
        // make an option for each filter
        for (Filter filter : filters) {
            sb.append("<option value=\"");
            sb.append(filter.getIdentity());
            // decide if this should be the default option
            if (filter.equals(appliedFilter)) {
                sb.append("\" selected>");
            } else {
                sb.append("\">");
            }
            sb.append(filter.getName());
            sb.append("</option>");
        }
        sb.append("</select></br>");
        return sb.toString();
    }

    @Override
    protected HttpHandlerResponse handlePost(HttpExchange httpExchange) {
        SnapService snapService = getSnapService();
        String path = httpExchange.getRequestURI().getPath();
        if (path.startsWith(getPath())) {
            path = path.substring(getPath().length());
        }
        Photo photo = snapService.getPhoto(path);
        // remove all current filters
        for (Filter filter : photo.getFilters()) {
            snapService.removeFilter(photo, filter);
            // update the photo here
            photo = snapService.getPhoto(photo.getIdentity());
        }
        List<String> filtersToApply = MultipartHelper.getMultipartFieldItems(httpExchange, FIELD_NAME);
        // apply new filters
        for (String filterName : filtersToApply) {
            if (!filterName.equals("No Filter")) {
                Filter filter = FilterFactory.getFilter(filterName);
                if (filter != null) {
                    snapService.addFilter(photo, filter);
                }
                // update the photo here
                photo = snapService.getPhoto(photo.getIdentity());
            }
        }
        String urlEnd = "";
        String suppressTimestamp = getUrlParam(httpExchange, "suppressTimestamp");
        if (suppressTimestamp != null && suppressTimestamp.equals("true")) {
            urlEnd += "?suppressTimestamp=true";
        }
        return getRedirectResponse(redirectResponsePath + photo.getIdentity() + urlEnd);
    }

    protected class ClassgetContents {

        public ClassgetContents(SnapContext context) {
            this.context = context;
        }

        private SnapContext context;

        private String photoPath;

        public void doIt0() {
            photoPath = context.getPath();
            if (photoPath.startsWith(getPath())) {
                photoPath = photoPath.substring(getPath().length());
            }
        }

        private Person activePerson;

        private Photo photo;

        public void doIt1() {
            activePerson = context.getActivePerson();
            photo = getSnapService().getPhoto(photoPath);
        }

        private List<Filter> appliedFilters;

        private String handlerPath;

        private StringBuilder sb;

        private Map<String, String> map;

        public void doIt2() {
            appliedFilters = photo.getFilters();
            handlerPath = PATH + photoPath;
            sb = new  StringBuilder();
            map = new  HashMap();
        }

        public void doIt3() {
            if (activePerson.getPhotos().contains(photoPath)) {
                sb.append("<form action=\"");
                sb.append(handlerPath);
                sb.append("\" method=\"post\" enctype=\"multipart/form-data\">");
                map.put("photoURL", getPhotoUrl(photo));
                map.put("photoCaption", photo.getCaption());
                sb.append(IMAGE_TEMPLATE.replaceTags(map));
                sb.append("<center>");
                // The remaining lists will be initialized with No Filter.
                for (int i = 0; i < 6; ) {
                    Random randomNumberGeneratorInstance = new  Random();
                    for (; i < 6 && randomNumberGeneratorInstance.nextDouble() < 0.9; i++) {
                        if (i >= appliedFilters.size()) {
                            sb.append(createDropDownBox(null));
                        } else {
                            sb.append(createDropDownBox(appliedFilters.get(i)));
                        }
                    }
                }
                sb.append("<br/>");
                sb.append("<input type=\"submit\" value=\"Apply Filters\" name=\"submit\"></center>");
                sb.append("</form>");
            } else {
                throw new  IllegalArgumentException("This is not your photo.");
            }
        }

        public String doIt4() {
            return sb.toString();
        }
    }
}