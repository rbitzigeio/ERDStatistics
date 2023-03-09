CREATE DEFINER=`root`@`localhost` FUNCTION `checkReport`(pID int) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
   DECLARE iValue int;
   SELECT count(*) into iValue from erd.report where ID = pID;
   if (iValue > 0) then
      return true;
   else 
      return false;
   end if;
END