# Geogaddi

Geogaddi is a command-line tool that parses and transforms CSV data.

## Dependencies
- Java

Building requires Maven

Initial setup (to build and resolve dependencies):
```shell
mvn install
```

An executable JAR with dependencies will deploy to the target/ directory.

###Arguments
- -a : perform both a fetch and transform operation
- -f : perform only the fetch operation
- -t : perform only the transform operation
- -c : clean the output directory before writing to it, will also empty the target S3 bucket
- -u : unzip the fetched file and worked with uncompressed data
- -p path/to/geogaddi.properties : location of Java-formatted properties file
- -j path/to/geogaddi.json : location of JSON properties file

####Examples
Some example usages: 
```shell
java -jar geogaddi.jar -c -a -j geogaddi.json
```
Will clean the output directory, perform both a fetch and transform operations, and will use geogaddi.json to define the properties.

```shell
java -jar geogaddi.jar -t -p geogaddi.properties
```
Will perform a transform operation on the CSVs defined in the Java-format properties file.

###Properties (Java format)
- fetcher.source.url : comma-separated list of URLs of gzipped CSVs to download
- fetcher.dump.dir : directory where the fetcher should download the data
- parceler.source.csv : normally, the fetcher will tell the parceler where to find the downloaded CSVs, but in the case of running a transform-only operation, this comma-separated list should be defined to tell the parceler what files should be used
- parceler.parcel.whitelist.source : path to a list of elements that should be used as a whitelist (for example, a list of desired stations); if unprovided, no filtering will take place
- parceler.parcel.whitelist.filter.index : the CSV column index that should be used in conjunction with the whitelist for filtering
- parceler.parcel.folder.index : CSV column index that will be used to define the output directory structure; each distinct element in this column will have its own directory built
- parceler.parcel.file.index : CSV column index that will be used to define the output file structure within the directories defined above; each distinct element in this column will have its own file built
- parceler.parcel.data.index : comma-separated list of CSV column indices that will be used to define the output data pattern
- parceler.output.dir : path to the output from the transform operation
integrator.s3.accesskeyid : AWS access Key ID - see notes below
integrator.s3.secretkey : AWS secret key - see notes below
integrator.s3.bucket.name : AWS S3 bucket, will create if it doesn't exist; policies are not applied

###Properties (JSON format)
```json
{
	"fetcher": {
		"source": [
			// array of URLs of gzipped CSVs to download
		],
		"dumpDir": "data/dump" // directory where the fetcher should download the data
	},
	"parceler": {
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
-> DATE,VALUE ...
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
-> VALUE,DATE ...
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

Set up the credentials as [described here](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-setup.html). BUT ALSO! In the IAM console, also explicitly give Amazon S3 Full Access permissions to the user with the generated credentiails. Administrator Access is not sufficient!

To make the bucket contents public, set the following bucket policy from the S3 console
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

## Todo
### 0.1
- Exit program cleanly when using integrator
- Block bucket destruction, then create