import java.io.*;
import java.text.*;
import java.util.*;

public class Reader {
    public static void main(String[] args) {
        String filePath = "Assignment_Timecard.xlsx - Sheet1.csv";


        EmployeeRecords(filePath);
    }

    public static void EmployeeRecords(String filePath) {
        BufferedReader buffer = null;
        String line;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date prevDate = null;

        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String[] headers = buffer.readLine().split(",");


            int nameIndex = -1;
            int positionIndex = -1;
            int timeIndex = -1;
            int timeOutIndex = -1;
            int timeHourIndex = -1;
            int start_Date_Index = -1;
            int end_Date_Index = -1;


            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals("Employee Name")) {
                    nameIndex = i;
                } else if (headers[i].equals("Position ID")) {
                    positionIndex = i;
                } else if (headers[i].equals("Time")) {
                    timeIndex = i;
                } else if (headers[i].equals("Time Out")) {
                    timeOutIndex = i;
                } else if (headers[i].equals("Timecard Hours (as Time)")) {
                    timeHourIndex = i;
                } else if (headers[i].equals("Pay Cycle Start Date")) {
                    start_Date_Index = i;
                } else if (headers[i].equals("Pay Cycle End Date")) {
                    end_Date_Index = i;
                }
            }

            while ((line = buffer.readLine()) != null) {
                String[] values = line.split(",");


                if (values.length <= Math.max(nameIndex, Math.max(positionIndex, timeIndex))) {
                    continue; // Skip incomplete records
                }

                String name = values[nameIndex].trim();
                String position = values[positionIndex].trim();
                String timeStr = values[timeIndex].trim();
                String timeOutStr = values[timeOutIndex].trim();
                String timeHour = values[timeHourIndex].trim();

                Date time = dateFormat.parse(timeStr);


                Date timeOut = dateFormat.parse(timeOutStr);


                if (hasWorkedConsecutiveDays(filePath, time) ||
                        (prevDate != null && timeDifference(prevDate, time) > 1 && timeDifference(time, prevDate) < 10) ||
                        (timeDifference(time, timeOut) > 14)) {
                    System.out.println("Name: " + name + ", Position: " + position);
                }

                prevDate = time;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean hasWorkedConsecutiveDays(String filePath, Date currentDate) throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        Date prevDate = null;

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values.length <= 2) {
                continue;
            }

            String timeStr = values[2].trim();
            Date date = dateFormat.parse(timeStr);

            if (prevDate == null) {
                prevDate = date;
            } else if (date.getTime() - prevDate.getTime() == 24 * 60 * 60 * 1000) {

                prevDate = date;
            } else {
                prevDate = null;
            }if (prevDate != null && timeDifference(prevDate, currentDate) >= 7) {
                return true;
            }
        }

        return false;
    }


    public static double timeDifference(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();

        return diff / (60.0 * 60.0 * 1000);
    }
}
