# ERDStatistic
Statistic of used Bandwidth of Express Route Direct in Azure.
Daily and period selection is possible. Bandwidth for In-Bound, means traffic into Azure, and Out-Bound, traffic from Azure to OnPrem system, is visible. Data comes as comma seperated value from Riverbed, a report that consumes information from the interface at the firewalls. Data will be stored in a MySql Database.

Use ant db-export to export data, structure and storedprocedure. 
