package de.tu.darmstadt.lt.ner;

import org.apache.wicket.protocol.http.WebApplication;

public class ApplicationPageBase extends WebApplication {
    public ApplicationPageBase() {
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage() {
        return HomePage.class;
    }
}