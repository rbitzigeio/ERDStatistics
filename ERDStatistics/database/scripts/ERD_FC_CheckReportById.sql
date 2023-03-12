CREATE DEFINER=`root`@`localhost` FUNCTION `checkReportById`(pID int) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
   DECLARE iValue INT;
   SELECT count(*) into iValue from erd.report where ID = pID;
   IF (iValue > 0) then 
      return true;
   ELSE 
      return false;
   END IF;
END