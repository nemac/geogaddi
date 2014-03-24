# Geogaddi

Geogaddi is a command-line tool that parses and transforms CSV data. It separates out large CSV data aggregates that are sliced in one way into many smaller CSVs that may be sliced another way to be used by client map applications asynchronously.
Initially, Geogaddi has been built with retrieving and parsing GHCND data in mind.
The GHCND source files are hundreds of individual files separated by year (going back to 1763) and are updated daily with the latest data.
Usually only the current year file has data appended, but occasionally data may be revised from new models or missing values may be added.
For the most recent years, each file is several gigabytes of data and therefore unusable by most clients.
Moreover, each file contains all stations and all variables, when only a handful may be of relevence.

Geogaddi takes these large source files, filters them, and parcels them out into smaller aggregates that can span many years.

Example input
```shell
2014.CSV
-> USC00273626,20140225,TMIN,-106,,,H,1800
-> USC00273626,20140225,TOBS,-56,,,H,1800
-> ...
```

Example output
```shell
USC00273626/TMIN.csv
-> 20140225,-106
-> ...
```

Additionally, a summary JSON file is generated in the output root that gives a quick key in the following format:

```json
{ 
	"FOLDER1": {
		"FILE1": {
			"min": "",
			"max": ""
		},
		"FILE2": {
			"min": "",
			"max": ""
		},
		...
	}, 
	"FOLDER2": {
		"FILE1": {
			"min": "",
			"max": ""
		},
		"FILE2": {
			"min": "",
			"max": ""
		},
		...
	},
	...
}
```

where the file min and max are the minimum and maximum values of the first data column in the output CSV.

Most typically for the above format, a FOLDER will be a station (e.g., USC00273626), a FILE will be some variable (e.g., TMIN), and the min and max variables will represent the beginning and end dates respectively of data contained in the file (e.g., "min": "20000101", "max": "20140324").
But these examples are just one configuration. All folder, file, and contents arrangements can be defined in the properties, as described below.


## Dependencies
- Java

Building requires Maven

Initial setup (to build and resolve dependencies):
```shell
mvn install
```

An executable JAR with dependencies will deploy to the target/ directory.

###Arguments
| Flag | Parameter                   | Description                                                                           |
|------|-----------------------------|---------------------------------------------------------------------------------------|
| -a   |                             | perform both a fetch and transform operation                                          |
| -f   |                             | perform only the fetch operation                                                      |
| -t   |                             | perform only the transform operation                                                  |
| -i   |                             | perform only the integration operation                                                |
| -c   |                             | clean the output directory before writing to it, will also empty the target S3 bucket |
| -u   |                             | unzip the fetched file and worked with uncompressed data                              |
| -p   | path/to/geogaddi.properties | location of Java-formatted properties file                                            |
| -j   | path/to/geogaddi.json       | location of JSON properties file                                                      |

Note: if the properties are set to override, only the -j or -p flags will be used. The remaining options will be driven from the properties file.

####Examples
Some example usages: 
```shell
java -jar geogaddi.jar -c -a -j geogaddi.json
```
Will clean the output directory, perform fetch, transform, and integrate operations, and will use geogaddi.json to define the properties.

```shell
java -jar geogaddi.jar -t -p geogaddi.properties
```
Will perform a transform operation on the CSVs defined in the Java-format properties file.

```shell
java -jar geogaddi.jar -j geogaddi.json
```
If "override" is set to true in the properties file, this is all that's required from the command-line, and all instructions will be defined in the properties.



###Properties (Java format)
| Property           | Description                                              |
|--------------------|----------------------------------------------------------|
| fetcher.source.url | comma-separated list of URLs of gzipped CSVs to download |
| fetcher.dump.dir | directory where the fetcher should download the data |
| parceler.source.csv | normally, the fetcher will tell the parceler where to find the downloaded CSVs, but in the case of running a transform-only operation, this comma-separated list should be defined to tell the parceler what files should be used |
| parceler.parcel.whitelist.source | path to a list of elements that should be used as a whitelist (for example, a list of desired stations); if unprovided, no filtering will take place |
| parceler.parcel.whitelist.filter.index | the CSV column index that should be used in conjunction with the whitelist for filtering |
| parceler.parcel.folder.index | CSV column index that will be used to define the output directory structure; each distinct element in this column will have its own directory built |
| parceler.parcel.file.index | CSV column index that will be used to define the output file structure within the directories defined above; each distinct element in this column will have its own file built |
| parceler.parcel.data.index | comma-separated list of CSV column indices that will be used to define the output data pattern |
| parceler.output.dir | path to the output from the transform operation |
| integrator.source.dir | normally, the parceler will tell the integrator where to find the output data dir, but in the case of running an integrate-only operation, this is the path of the directory to put into S3 |
| integrator.s3.accesskeyid | AWS access Key ID - see notes below |
| integrator.s3.secretkey | AWS secret key - see notes below |
| integrator.s3.bucket.name | AWS S3 bucket, will create if it doesn't exist; policies are not applied |

###Properties (JSON format)
```json
{
	"override": true, // the operation will be driven from the properties file, only -j or -p flags are used
	"useAll": false, // the operation will perform all steps, overrides individual element booleans
	"fetcher": {
		"enabled": true, // will use the fetcher
		"uncompress": false, // if true, will uncompress the fetched file and clean up the downloaded .gz
		"source": [
			// array of URLs of gzipped CSVs to download
		],
		"dumpDir": "data/dump" // directory where the fetcher should download the data
	},
	"parceler": {
		"enabled": true, // will use the parceler,
		"uncompress": false, // currently unused
		"cleanSource": false, // will delete the parceler source once it is done reading the files
		"cleanDestination": false, // will clear the output directory before writing
		"existingFromIntegrator": false // currently unused
		"sourceCsv": [
			// list of CSVs used for a transform operation, if the fetcher is not used
		],
		"whiteList": "", // path to list of a whitelist of items for filtering
		"whiteListIndex": 0, // CSV column index to filter with list
		"folderIndex": 0, // CSV column index for distinct elements that will define the output directory structure
		"fileIndex": 2, // CSV column index for distinct elements that will define the output file structure within the directories above
		"dataIndex": [1,3], // array of indices for the output data pattern
		"outputDir": "data/output" // path to output from the transform operation
	},
	"integrator": {
		"enabled": false, // will use the integrator
		"cleanSource": false // currently unused
		"awsAccessKeyId": "", // AWS access Key ID - see notes below
		"awsSecretKey": "", // AWS secret key - see notes below
		"bucketName": "" // AWS S3 bucket, will create if it doesn't exist; policies are not applied
	}
}
```

####Examples
Input CSV format
```shell
STATION,DATE,VARIABLE,VALUE
```

Properties 1
```java
parceler.parcel.folder.index=0
parceler.parcel.file.index=2
parceler.parcel.data.index=1,3
```
	
Output 1
```shell
STATION/VARIABLE.csv
-> DATE,VALUE
-> DATE,VALUE
-> ...
```

Properties 2
```java
parceler.parcel.folder.index=2
parceler.parcel.file.index=0
parceler.parcel.data.index=3,1
```

Output 2
```shell
VARIABLE/STATION.csv
-> VALUE,DATE
-> VALUE,DATE
-> ...
```

Note that the output will always be in ascending order with the entire line viewed as a single string. So for the first example properties, the output will look like the following:

```shell
...
17641227,64
17641228,66
17641229,55
17641230,58
17641231,69
...
```

Whereas this same block in the second example properties would look like:

```shell
...
55,17641229
58,17641230
64,17641227
66,17641228
69,17641231
...
```

Therefore, it usually makes the most sense to have date (or something equally meaningful) output in the first column.

###Working with AWS S3
Some things to note to get this to work correctly with S3.

####Integrator permissions
1. Set up the credentials as [described here](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-setup.html). 
2. In the IAM console, also explicitly give Amazon S3 Full Access permissions to the user with the generated credentials. Administrator Access is not sufficient!

####Making bucket public
1. Go to S3 Management Console
2. Select the bucket -> Properties
3. Permissions -> Add (or Edit) bucket policy
4. Set some variant of the following policy:

```json
{
  "Version":"2012-10-17",
  "Statement":[{
    "Sid":"AllowPublicRead",
        "Effect":"Allow",
      "Principal": {
            "AWS": "*"
         },
      "Action":["s3:GetObject"],
      "Resource":["arn:aws:s3:::changeThisToYourBucketName/*"
      ]
    }
  ]
}
```

####CORS configuration
Even if the bucket contents are public, any machines attempting to access the files will run into issues with cross-origin resources. To resolve:

1. Go to S3 Management Console
2. Select the bucket -> Properties
3. Permissions -> Add (or Edit) CORS Configuration
4. Set some variant of the following configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
    <CORSRule>
        <AllowedOrigin>*</AllowedOrigin>
        <AllowedMethod>GET</AllowedMethod>
        <MaxAgeSeconds>3000</MaxAgeSeconds>
        <AllowedHeader>Authorization</AllowedHeader>
    </CORSRule>
</CORSConfiguration>
```

####DNS mapping
See [this link](https://docs.aws.amazon.com/AmazonS3/latest/dev/website-hosting-custom-domain-walkthrough.html) for mapping S3 to a custom domain name.

## Todo
### 0.1
- (RD) Exit program cleanly when using integrator

###0.2
- (RD) Use GZ end to end
	- (C) Then enable S3 GZ content-encoding header in integrator
- (RD) Add all new JSON properties to Java properties format handler

###0.3
- If needed (i.e., if the current architecture becomes costly), add option to get backlog update source from S3 as opposed to relying on the machine hosting the script to maintain a copy of the output
	- **CURRENT ARCHITECTURE**
		1. EBS (or script runner disk) contains:
			- operating system
			- Java
			- script
			- at least the fully parceled output data product snapshot
		2. EC2 (or script runner) spools up to process the updates, reading EBS (or logical disk)
		3. downloads update from NCDC
		4. hashes the entire update from NCDC and updates the entire local output data product snapshot with new values from downloaded update
		5. transfers the entire local snapshot to S3
	- **FUTURE ARCHITECTURE**
		1. EBS (or script runner disk) contains:
			- operating system
			- Java
			- script
		2. EC2 (or script runner) spools up to process the updates, reading EBS (or logical disk)
		2. downloads update from NCDC
		4. hashes the entire update from NCDC, loops through the hash and
			1. makes local copy of each S3 file
			2. updates file locally
			3. pushes to S3
			
###n.n
- If S3 supports appending to files, take advantage of it (currently not possible)