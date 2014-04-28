# Geogaddi

Geogaddi is a command-line tool that parses and transforms CSV data. It separates out large CSV data aggregates sliced one way into many smaller CSVs sliced another way and to be used by client map applications asynchronously.
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

The parceled files do not have to be overwritten on update, but are usually appended to as new data arrive. Additionally, a module is provided to push all of the files to S3 to make them publicly available for map viewers and other clients.

## Dependencies
- Java

Building requires Maven

Initial setup (to build and resolve dependencies):
```shell
mvn install
```

An executable JAR with dependencies will deploy to the target/ directory.

###Properties (JSON format)
```json
{
	"quiet": true, // silence STDOUT status messages
	"useAll": false, // the operation will perform all steps, overrides individual element booleans
	"uncompress": // all operations will uncompress files, expect uncompressed files from previous steps, and write out uncompressed files
	"fetcherOptions": {
		"enabled": true, // will use the fetcher
		"sources": [
			// array of URLs of gzipped CSVs to download
		],
		"dumpDir": "data/dump" // directory where the fetcher should download the data
	},
	"parcelerOptions": {
		"enabled": true, // will use the parceler,
		"cleanSource": false, // will delete the parceler source once it is done reading the files
		"cleanDestination": false, // will clear the output directory before writing
		"sourceCSVs": [
			// list of CSVs used for a transform operation, if the fetcher is not used
		],
		"folderWhiteList": "stations.csv", // path to list of a whitelist of items for filtering the folder variable, will not filter if not provided
		"folderWhiteListIndex": 0, // CSV column index to filter with list
		"folderIndex": 0, // CSV column index for distinct elements that will define the output directory structure
		"fileWhiteList": "vars.csv", // path to list of a whitelist of items for filtering the file variable, will not filter if not provided
		"fileWhiteListIndex": 2, // CSV column index to filter with list
		"fileIndex": 2, // CSV column index for distinct elements that will define the output file structure within the directories above
		"dataIndexes": [1,3], // array of indices for the output data pattern
		"outputDir": "data/output" // path to output from the transform operation
	},
	"deriverOptions": {
		"enabled": true, // will use the deriver
		"sourceDir": "data/output", // directory for data that serves as the derived product basis
		"transformationOptions": [ // array of multiple transformations to be performed, each will have its own output
			{
				"name": "First Transformation registered", // name for logging purposes
				"transformationSourceLib": "", // path to library, if the transformation logic class is external to Geogaddi
				"transformation": "YTDCumulative", // class name for the transformation
				"file": "PRCP", // file pattern to be used for the source data files (will ignore extensions in data product)
				"normalDir": "data/normals", // path to normals to be used for filling in gaps in transformation, if a normal isn't found, the product is skipped
				"outName": "PRCP_YTD" // output name pattern; will match the general output extension (csv.gz if compressed, csv if uncompressed)
			},
			{
				"name": "Second Transformation in some directory",
				"transformationSourceLib": "C:/full/path/to/lib.jar",
				"transformation": "com.foo.TransformationClass2",
				"file": "PRCP",
				"normalDir": "data/normals",
				"outName": "PRCP_YTD"
			},
			{
				"name": "Thrid Transformation on classpath",
				"transformationSourceLib": "",
				"transformation": "com.foo.TransformationClass3",
				"file": "PRCP",
				"normalDir": "data/normals",
				"outName": "PRCP_YTD"
			}
		]
	},
	"integratorOptions": {
		"enabled": false, // will use the integrator
		"cleanSource": false, // currently unused,
		"sourceDir": "data/output", // directory for output files to be transferred
		"awsAccessKeyId": "", // AWS access Key ID - see notes below
		"awsSecretKey": "", // AWS secret key - see notes below
		"bucketName": "" // AWS S3 bucket, will create if it doesn't exist; policies are not applied
	}
}
```


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

###Deriving products
Geogaddi provides a flexible framework for generating derived products.
Derived products are generated in the order that they are defined in the deriverOptions.transformationOptions array. 
There will eventually be an increased number of transformation classes specified within the Geogaddi project, but incorporating them requires the project to be recompiled.
Those classes may be defined by calling the named transformation in the options, with the transformationSourceLib not specified.
To provide further flexibility, transformation classes may be specified with a classpath reference or by referencing the path to a JAR that contains the desired transformation class.
All transformation classes must extend the AbstractTransformation class.
There are several methods within the derived product generation process flow that may be overridden to specify the desired logic.

####General product flow
For each transformation defined, a similar series of steps take place to generate new products.
Normals and the source data are converted to KV maps that are sent to the process method.
Generally the source data is formatted as follows:

```shell
STATION/VARIABLE.csv
-> DATE,VALUE
-> DATE,VALUE
-> ...
```

while the normals are formatted as follows for only one year:

```shell
VARIABLE/STATION.csv
-> DATE,VALUE
-> DATE,VALUE
-> ...
```

The normal file describes the expected value for that day of the year.
As the data often have gaps in the period of record, the normal file is consulted to fill in values.
If a normal file is not discovered that station is skipped.

####Transformation process flow
Based on the properties for a desired transformation, a transformation factory provides the logic required to process a product using a codified process flow.
Therefore, every transformation is expected to extend the AbstractTransformation class.
Within the process flow, only the necessary methods need to be overridden to perform the logic required.

The process is as follows:

1. transformNormals: simply passes through in the superclass, override to perform some logic on each normal value before it is combined with the source data.
E.g., for YTD_PRCP, the normals are in 100ths of inches, but the data are in centimeters. 
Each normal value must thus be multiplied by 25.4 before it is directly comparable to the source data.

2. fillGapsWithNormals: merges the source data with the normal data. 
Two KV maps enter, one KV map leaves. 
E.g., the source data has values for 20140101, 20140102, 20140104. It is detected that 20140103 is missing, so the 0103 value is consulted from the normal file and filled into the output map.

3. transform: the merged KV map that has a value for every day in the period of record may then be iterated over and transformed. Again a single KV map is returned.

In the case of the YTDCumulative, the normals must be transformed and the data are iterated over in the transform method to provide a running total. 
The method fillGapsWithNormals is not overridden because the default behavior is sufficient.

###Integrating with AWS S3
Some things to note when integrating with S3.

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
Even if the bucket contents are public, any machines attempting to access the files are likely to run into permission issues with cross-origin resources. To resolve:

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
###0.3
- Improve the deriver to not clobber the summary JSON file if used without the parceler.
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
