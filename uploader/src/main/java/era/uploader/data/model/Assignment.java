package era.uploader.data.model;

import java.util.Set;
import java.util.HashSet;

public class Assignment {
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private Set<Page> pages = new HashSet<>();  /* Set of Page objects for each Assignment */

    /* Constructor */
    public Assignment(
            String imageFilePath,
            String name,
            Set<Page> pages
    ) {
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.pages = pages;
    }

    /* Getters and Setters */
    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void setPages(Set<Page> pages) {
        this.pages = pages;
    }
}
