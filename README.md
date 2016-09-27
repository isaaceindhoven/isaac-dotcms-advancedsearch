# dotCMS Advanced Search

Ever wondered if you replaced all "Lorem ipsum" bogus text before you put a site live? Ever wanted to know what pieces of content contain that specific word you want to remove from your website? We have. Sometimes you need to find a text in dotCMS but you don't know where it is used. It could be in a container, template, folder, text file or content... That's where the Advanced Search comes in. It allows you to search for text and regex in all those places.

Previous releases of this plugin for older dotCMS versions can be found [here](../../releases).

## Features

* Search for text or regex in content, containers, templates, structures, folders and/or files.
* Search on a specific host or in the entire dotCMS.
* Search in published, unpublished and/or archived content.
* Search for working and/or live versions.
* Directly edit the found text.
* Remembers the last search and the results.
* Displays a short snippet containing the searched text.
* Displays a description of where the text is found (which content field, for instance).

The plugin searches for the text in many places, including the database. It's therefore not recommended to give clients access to the portlet. Excessive use of the search could temporarily slow down dotCMS.

![](https://cloud.githubusercontent.com/assets/10976988/18436015/8dcdd680-78f6-11e6-8866-10597282b319.png)

## Installation

To use it, you obviously first need to install the plugin. To install it take these steps:

* Clone this repository.
* Open the console and go the the folder containing pom.xml
* Execute the following maven command: **mvn clean package**
* The build should succeed and a folder named "target" will be created.
* Open the "target" folder and check if the **.jar** file exists.
* Open dotCMS and go to the dotCMS Dynamic Plugins page by navigating to "System" > "Dynamic Plugins".
* Click on "Exported Packages" and add these packages to the list (make sure to add a comma to the last package in the existing list):

```java
org.osgi.framework,
com.dotmarketing.osgi,
com.dotmarketing.beans,
com.dotmarketing.portlets.containers.business,
com.dotmarketing.portlets.containers.model,
com.dotmarketing.portlets.fileassets.business,
com.dotmarketing.portlets.folders.business,
com.dotmarketing.portlets.folders.model,
com.dotmarketing.portlets.htmlpages.business,
com.dotmarketing.portlets.htmlpages.model,
com.dotmarketing.portlets.languagesmanager.business,
com.dotmarketing.portlets.languagesmanager.model,
com.dotmarketing.portlets.templates.business,
com.dotmarketing.portlets.templates.model,
com.liferay.portal
```

* Click on "Save Packages".
* Click on "Upload Plugin" and select the .jar file located in the "/target" folder.
* Click on "Upload Plugin" and the plugin should install and automatically start.

That's it, you've installed the plugin and can use the Advanced Search Portlet.

## Usage

To use this plugin, we have to add the Advanced Search Portlet to a tab.

* Open dotCMS and go to the Roles & Tabs page by navigating to "System" > "Roles & Tabs".
* On the left sidemenu click on "System" and select the user you want to create the tab for.
* The page should refresh and you will see a small tabmenu followed by a list of existing tabs.
* Click on the "CMS Tabs" link from the tabmenu.
* At the top-right corner click on "Create Custom Tab", a pop-up should appear.
* Name and order the tab, and select the "Advanced Search" tool from the tools drop-downlist.
* Click on "Add" and finally hit "Save".
* The tab should be added to the list of existing tabs.
* Check the created tab from the list and click on "Save".
* Refresh the page and the tab should be added to the dotCMS tabmenu.
* Click on the newly created tab and start searching!

## Meta

[ISAAC - 100% Handcrafted Internet Solutions](https://www.isaac.nl) – [@ISAAC](https://twitter.com/isaaceindhoven) – [info@isaac.nl](mailto:info@isaac.nl)

Distributed under the [Creative Commons Attribution 3.0 Unported License](https://creativecommons.org/licenses/by/3.0/).
