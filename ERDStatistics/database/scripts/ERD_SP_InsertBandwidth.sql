CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBandwidth`(IN uTime int, IN fTime timestamp, IN bitsIn double, IN bitsOut double, IN reportID int, IN itSystem int)
BEGIN 
   INSERT INTO ERD.Bandwidth (UnixTime, FormattedTime, BitsPerSecIn, BitsPerSecOut, ReportID, ITSystem)
   VALUES (uTime, fTime, bitsIn, bitsOut, reportID, itSystem);
END