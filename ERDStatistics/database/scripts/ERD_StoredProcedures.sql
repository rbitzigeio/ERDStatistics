CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBandwidth`(IN uTime int, IN fTime date, IN bitsIn double, IN bitsOut double)
BEGIN 
   INSERT INTO ERD.Bandwidth (UnixTime, FormattedTime, BitsPerSecIn, BitsPerSecOut) 
   VALUES (uTime, fTime, bitsIn, bitsOut);
END