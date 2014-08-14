# Geogaddi Climate Explorer Setup

## Data Sets

### GHCND

The [Climate Explorer](https://github.com/nemac/climate-explorer) application makes use of
the GHCND data set from NCDC (ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/readme.txt).  It relies
on the data being stored in gzipped files, with each file containing data for one variable from one station.

Climate Explorer expects the GHCND data files to be organized into a separate subdirectory for each
station, named according to the station id, with the data for each element in a gzipped CSV file named
according to the element name.  For example, the TMAX data for the station with id "USC00012675"
should be in the file "USC00012675/TMAX.csv.gz"; this file contains all the TMAX values
for station USC00012675 present in the data set, for the entire history of the station.

The current version of Climate Explorer uses only the GHCND elements TMAX, TMIN, and PRCP, although more
may be added in the future, and this is easily controlled via a configuration file (see below).

Climate Explorer also uses a cumulative year-to-date total precipitation element that is based on the
GHCND PRCP element.  Climate Explorer expects the data for this ytd precipitation element
to be available in files named acoording to the same pattern as the other GHCND elements; the
name used for this element is "PRCP_YTD".  So, for example, the values for station
USC00012675 would be in the file "USC00012675/PRCP_YTD.csv.gz"

NCDC distributes the GHCND data in files available via ftp, and these files are updated each day with new
data files.  The files available from NCDC are not structured according to the above conventions required by
Climate Explorer.  Geogaddi can be configured to run on a daily basis to take care of downloading the updated
file(s) from NCDC each day and transforming the data into the proper file structure and content as required
by Climate Explorer.  Geogaddi attempts to do this as efficiently as possible, in the sense that it only
modifies files for which new data needs to be inserted, and if the new data is being inserted at the end of
the file, it appends to the file rather than reading and re-writing the entire file.

Geogaddi also takes care of generating the ytd precipitation files from the original daily precipitation
total (PRCP) files.


### 2010 Normals Data

Climate Explorer also makes use of NCDC's 2010 Normals product (ftp://ftp.ncdc.noaa.gov/pub/data/normals/1981-2010).
This data should be stored in files of the form "NORMAL_ELEMENT_ID/STATION_ID.csv.gz", where NORMAL_ELEMENT is
one of "NORMAL_TMAX", "NORMAL_TMIN", or "NORMAL_YTD_PRCP".  So, for example, the normals data for the TMAX element
for station USC00012675 would be in the file "NORMAL_TMAX/USC00012675.csv.gz".

Since the normals data generally does not change, Geogaddi is not configured to download and/or update it on a
regular basis.  The normals data is simply a part of the data directory maintained by Geogaddi so that
Climate Explorer can access it in the same way that it accesses the GHCND data.

## Geogaddi Installation and Setup

The steps involved in setting up a Geogaddi instance to support Climate Explorer are as follows:

1. Download and compile Geogaddi to build the executable jar file
2. Initialize the Geogaddi output archive on a publicly visible web server
3. Edit the Geogaddi configuration files with the appropriate pathnames and/or other settings
4. Run Geogaddi manually to test it
5. Arrange to run Geogaddi as a cron job, or other regularly scheduled process, once each day
6. Configure the CORS authorization in your web server so that JavaScript clients may download the file
7. Perform Annual Maintenance each January to update to the next year

The following sections give more detailed instructions for each of these steps.

### 1. Download and Compile Geogaddi

The Geogaddi code can be obtained from Github: https://github.com/nemac/geogaddi.  It is written in Java
and requires Maven for building.  To build it, just type "mvn install".  This doesn't actually install
anything outside the source directory --- it simply creates an executable jar file in the "target" subdirectory.

Geogaddi requires Java 1.7 or higher.

### 2. Initialize the Geogaddi Output Archive

The point of Geogaddi is to maintain a collection of data files which store GHCND data in a particular structure.  Each day,
when Geogaddi runs, it downloads the new GHCND data file from NCDC, and updates all the files in its collection with any
new data contained in that file.  Before Geogaddi can be run on a daily basis to update these files, however, the files need to be
created in the first place and initialized with all the data up to the current date.

In principle this initial populating of Geogaddi's output archive can be done by Geogaddi itself, by configuring it to download
and process all the years of GHCND data from the beginning of the data set.  This process is very computationally intense, however,
and can take several days to run. Therefore, the easiest way to initialize the output archive is to simply obtain a copy
of it from someone else who is already using Geogaddi, and populate your archive using that copy.

If you would like to use Geogaddi, contact the authors and we can help you get a copy of the archive.  The archive
will contain two subdirectories.  The "ghcnd" subdirectory contains the GHCND files, organized into subdirectories named
by station id, as described above, and the "normals" subdirectory contains the 2010 normals data, organized into
subdirectories by element name as described above.

You should put this archive of gzipped csv files in a location that is visible on a public web server, and that is also
accessible to the system where you intend to run the daily Geogaddi cron job.

In the rest of this documentation, we will use the term `ARCHIVE` to refer to the location where you have installed this
archive.  So, for example, the GHCND data for TMAX for station USC00012675 would be in the file
"`ARCHIVE`/ghcnd/USC00012675/TMAX.csv.gz", and the TMAX normals data for that station would be
in the file "`ARCHIVE`/normals/NORMAL_TMAX/USC00012675.csv.gz".

### 3. Edit the Geogaddi Configuration Files

Geogaddi is conrolled by settings in a JSON file.  This JSON contains all the details about which GHCND data
file(s) to download from NCDC, the location of the archive of files to be maintained, which derived product
files to generate (in particular, the ytd precip element), and various other settings.  Before starting
your daily Geogaddi cron job, you should prepare a copy of this configuration file with the appropriate
settings.

You can start with the file
[ghcnd.json](https://github.com/nemac/geogaddi/blob/master/ClimateExplorer/ghcnd.json).  Below is
an annotated copy of that file, with comments indicating the edits you might need to make.  Note
that actual JSON files cannot contain comments, so don't copy/paste this copy of the file verbatim.

Note also that Geogaddi is orgnized into four different components:

* the "fetcher", which handles downloading the original data file(s)
* the "parceler", which parses the file(s) downloaded by the fetcher and updates
  the files in the archive with any new data
* the "deriver", which creates new elements from existing ones (this is the part
  that creates the PRCP_YTD files from the PRCP files
* the "integrator", which uploads the resulting archive files, including any derived element
  files, to Amazon S3.  Note that this last step is optional and is not needed if you are
  hosting the files on your own server.

```json
{
    "quiet": false, // set this to true to suppress Geogaddi's diagnostic/progress output
    "useAll": false,
    "uncompress": false,
    
    // The following section configures the fetcher:
    "fetcherOptions": {
        "enabled": true,
        // Change the "sources" list below to give the ftp URLs of GHCND data files to be
        // downloaded by Geogaddi. Generally this should be a single file for whatever
        // the current year is.  For a period of a few days or weeks at the beginning
        // of each January, however, you should set this to contain the file for the
        // current year as well as the file for the previous year.
        "sources": [
                "ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/2014.csv.gz"
        ],
        // Change "dumpDir" to be the path of a temporary directory where Geogaddi can store
        // the downloaded source file(s) listed above.  This should be either an absolute
        // path, or a path relative to the directory from which Geogaddi is run:
        "dumpDir": "data/tmp" 
    },
    
    // The following section configures the parceler:
    "parcelerOptions": {
        "enabled": true,
        "cleanSource": false,
        "cleanDestination": false,
        "existingFromIntegrator": false,
        // Change the "sourceCSVs" list below to give the downloaded locations of the
        // files listed in "fetcherOptions.sources" above.  The paths listed here should
        // be either absolute paths, or relative to the directory from which Geogaddi is
        // run:
        "sourceCSVs": [
            "data/tmp/2014.csv.gz"
        ],
        // Change folderWhiteList to be the location of a CSV file containing a list of the ids of the
        // stations that you want Geogaddi to maintain data for; any stations present in the downloaded
        // files which are not in this list will be ignored.  This should be either an absolute
        // path to the file, or a path relative to the directory from which Geogaddi is run:
        "folderWhiteList": "stations.csv",
        "folderWhiteListIndex": 0,
        "folderIndex": 0,
        // Change "fileWhiteList" to be the location of a CSV file containing a list of the ids of the
        // elements that you want Geogaddi to maintain data for; any elements present in the downloaded
        // files which are not in this list will be ignored.  This should be either an absolute
        // path to the file, or a path relative to the directory from which Geogaddi is run:
        "fileWhiteList": "vars.csv",
        "fileWhiteListIndex": 2,
        "fileIndex": 2,
        "dataIndexes": [1,3],
        // Change "outputDir" to be the location where you want Geogaddi to save the
        // files it writes.  This should be either an absolute path to the file, or a
        // path relative to the directory from which Geogaddi is run:
        "outputDir": "data/ghcnd"
    },
    
    // The following section configures the deriver:
    "deriverOptions": {
        "enabled": true,
        // "sourceDir" should be the path of the directory where Geogaddi's output files are;
        // this should be the same as "parcelerOptions.outputDir" above:
        "sourceDir": "data/ghcnd",
        "transformationOptions": [
            {
                "name": "YTD Cumultaive Precip",
                "transformationSourceLib": "",
                "transformation": "YTDCumulative",
                "folder": "",
                "file": "PRCP",
                // "normalDir" should be the path of the normals/NORMAL_YTD_PRCP directory in your
                // data archive; Geogaddi needs this when creating the YTD precip derived element:
                "normalDir": "data/normals/NORMAL_YTD_PRCP",
                "dateIndex": 0,
                "dataIndex": 1,
                "outName": "PRCP_YTD"
            }
        ]
    },
    
    // The following section configures the integrator
    "integratorOptions": {
        "enabled": false // A setting of false here turns off the integrator
    }
}
```

In addition to a "ghcnd.json" file like the above, you will also need the CSV files "stations.csv" and "vars.csv"
which give the GHCND station ids and variable ids to be stored.  You can get these files from the ClimateExplorer
subdirectory of the Geogaddi source code.  Your "ghcnd.json" file should give the locations of these files
in the "parcelerOptions.folderWhiteList" and "parcelerOptions.fileWhiteList" properties.

### 4. Run Geogaddi manually to test it

To run Geogaddi manually, use a command like the following

    java -jar .../geogaddi-0.0.1-SNAPSHOT-jar-with-dependencies.jar -j .../ghcnd.json
    
where the first `...` is the path of the directory containing the jar file, and the second `...` is
the path of the directory containing your ghcnd.json file.  Note Geogaddi will interpret
any relative paths inside the ghcnd.json relative to the directory where it is run.

Run Geogaddi with the "quiet" option set to `false` to see messages about what it is doing and to confirm
that it is correctly downloading and/or writing the desired files in the correct locations.  After you
are confident that it is working correctly, you can change the "quiet" setting to `true`.


### 5. Arrange to run Geogaddi as a Cron Job

Once you are sure that everything is working as desired, set up a cron job to invoke Geogaddi
once a day.


### 6. Configure the CORS authorization

Since Climate Explorer is a JavaScript application, it is subject to the usual cross-origin request restriction
enforce by all browsers.  In order to accomodate this, the Geogaddi files on your web server need to be
configured so that the server adds the appropriate CORS header line to the file.  On an Apache web server
this involves putting someting like the following in your Apache config file, or in a ".htaccess" that applies
to the directory tree containing the files:

    Header set Access-Control-Allow-Origin "*"
    
For more details on configuring CORS on your server, see http://enable-cors.org.    


### 7. Perform Annual Maintenance each January to update to the next year

Notice that the sample ghcnd.json file above has the filename of the 2014 GHCND data file
hardwired into it (in two places).  Each year when January 1 rolls around, you'll need to update
this filename to the next year.

Furthermore, for a period of a few (2-4?) weeks after January 1 each year, it's best to configure
Geogaddi to download and process the data files for both the current year's data, and
the previous year's, because the previous year's data file will continue to change a bit
at the beginning of the month, as it gets fleshed out with late-arriving data.  So, during this
time, set the "fetcherOptions.sources" and "parcelerOptions.sourceCSVs" lists to contain
both the current year and the previous year's file (separated by a comma).  After 2-4 weeks (?) it
should be safe to remove the previous year's file.



