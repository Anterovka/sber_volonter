import dayjs from "dayjs";

export const formatDate = (d?: dayjs.Dayjs): string => {
  const date = (d ? d.toDate() : new Date());
  const timeOffset = Math.abs(date.getTimezoneOffset() * 60000);
  return new Date( date.getTime() + timeOffset).toISOString().replace('Z', '');
};

