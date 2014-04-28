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
