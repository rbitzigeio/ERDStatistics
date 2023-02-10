DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBandwidth`(IN uTime int, IN fTime timestamp, IN bitsIn double, IN bitsOut double, IN reportID int)
BEGIN 
   INSERT INTO ERD.Bandwidth (UnixTime, FormattedTime, BitsPerSecIn, BitsPerSecOut, ReportID)
   VALUES (uTime, fTime, bitsIn, bitsOut, reportID);
END //
