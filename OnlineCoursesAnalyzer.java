import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class OnlineCoursesAnalyzer {

  List<Course> courses = new ArrayList<>();

  public OnlineCoursesAnalyzer(String datasetPath) {
      BufferedReader br = null;
      String line;
      try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        List<String> institutionNameList = new ArrayList<>();
        for (Course e : courses){
            if (!institutionNameList.contains(e.getInstitution())){
                institutionNameList.add(e.getInstitution());
            }
        }
        int[] participantResult = new int[institutionNameList.size()];
        for (Course e : courses){
            int i = institutionNameList.indexOf(e.getInstitution());
            participantResult[i] = participantResult[i] + e.getParticipants();
        }
        Map<String, Integer> temp = new HashMap<>();
        for (int i = 0; i < institutionNameList.size(); i++){
            temp.put(institutionNameList.get(i), participantResult[i]);
        }

        // Sorting hashmap by Key in alphabetical order.
        Set<String> keySet = temp.keySet();
        Object[] arrayKey = keySet.toArray();
        Arrays.sort(arrayKey);
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Object key : arrayKey){
            result.put(key.toString(), temp.get(key.toString()));
        }
        return result;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        // Input the institution-subject participants pair into a new class.
        List<CourseForQ2> coursesForQ2 = new ArrayList<>();
        for (Course e : courses){
            String temp = e.getInstitution() + "-" + e.getSubject();
            CourseForQ2 courseForQ2 = new CourseForQ2(temp, e.getParticipants());
            coursesForQ2.add(courseForQ2);
        }

        // Counting the participants for the same institution-subject.
        List<String> institutionSubjectNameList = new ArrayList<>();
        for (CourseForQ2 e : coursesForQ2){
            if (!institutionSubjectNameList.contains(e.getInstitution_CourseSubject())){
                institutionSubjectNameList.add(e.getInstitution_CourseSubject());
            }
        }
        int[] participantResult = new int[institutionSubjectNameList.size()];
        for (CourseForQ2 e : coursesForQ2){
            int i = institutionSubjectNameList.indexOf(e.getInstitution_CourseSubject());
            participantResult[i] = participantResult[i] + e.getParticipants();
        }
        Map<String, Integer> temp = new HashMap<>();
        for (int i = 0; i < institutionSubjectNameList.size(); i++){
            temp.put(institutionSubjectNameList.get(i), participantResult[i]);
        }

        // Sorting hashmap by value in descending order.
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(temp.entrySet());
        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<String> stringList = new ArrayList<>();
        for (Map.Entry<String, Integer> L : list){
            String[] tempString = L.toString().split("=");
            stringList.add(tempString[0]);
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String s : stringList) {
            result.put(s, temp.get(s));
        }
        //System.out.println(result);
        return result;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        // Get all the instructors' names.
        List<String> instructorList = new ArrayList<>();
        for (Course e : courses){
            String tempName = e.getInstructors();
            String[] tempNameArray = tempName.split(", ", -1);
            for (String s : tempNameArray){
                if (!instructorList.contains(s)){
                    instructorList.add(s);
                }
            }
        }

        // Find independent courses and co-developed courses by instructor's name.
        List<List<List<String>>> courseOfInstructor = new ArrayList<>();
        for (String n : instructorList){
            List<String> independentCourseTemp = new ArrayList<>();
            List<String> codependentCourseTemp = new ArrayList<>();
            for (Course e : courses){
                String name = e.getInstructors();
                String[] nameArray = name.split(", ", -1);
                List<String> nameList = Arrays.asList(nameArray);
                if (nameList.contains(n)){
                    if (nameList.size() == 1 && !independentCourseTemp.contains(e.getTitle())){
                        independentCourseTemp.add(e.getTitle());
                    } else if (nameList.size() != 1 && !codependentCourseTemp.contains(e.getTitle())){
                        codependentCourseTemp.add(e.getTitle());
                    }
                }
            }

            // Sort course title.
            Object[] arrayIndependentCourse = independentCourseTemp.toArray();
            Arrays.sort(arrayIndependentCourse);
            String[] arrayIndependent = new String[arrayIndependentCourse.length];
            for (int i = 0; i < arrayIndependentCourse.length; i++){
                arrayIndependent[i] = arrayIndependentCourse[i].toString();
            }
            List<String> independentCourse = Arrays.asList(arrayIndependent);
            Object[] arrayCodependentCourse = codependentCourseTemp.toArray();
            Arrays.sort(arrayCodependentCourse);
            String[] arrayCodependent = new String[arrayCodependentCourse.length];
            for (int i = 0; i < arrayCodependentCourse.length; i++){
                arrayCodependent[i] = arrayCodependentCourse[i].toString();
            }
            List<String> codependentCourse = Arrays.asList(arrayCodependent);

            List<List<String>> tempList = new ArrayList<>();
            tempList.add(independentCourse);
            tempList.add(codependentCourse);

            courseOfInstructor.add(tempList);
        }
        Map<String, List<List<String>>> result = new LinkedHashMap<>();
        for (int i = 0; i < instructorList.size(); i++){
            result.put(instructorList.get(i), courseOfInstructor.get(i));
        }
        return result;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        if (Objects.equals(by, "hours")){
            // Find all the titles.
            List<String> titleList = new ArrayList<>();
            for (Course e : courses){
                if (!titleList.contains(e.getTitle())){
                    titleList.add(e.getTitle());
                }
            }
            Double[] hours = new Double[titleList.size()];
            Arrays.fill(hours, 0.0);
            for (String t : titleList){
                for (Course e : courses){
                    if (Objects.equals(e.getTitle(), t)){
                        if (e.getTotalHours() > hours[titleList.indexOf(t)]){
                            hours[titleList.indexOf(t)] = e.getTotalHours();
                        }
                    }
                }
            }
            Map<String, Double> temp = new HashMap<>();
            for (String t : titleList){
                temp.put(t, hours[titleList.indexOf(t)]);
            }
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(temp.entrySet());
            list.sort(new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if (o2.getValue().compareTo(o1.getValue()) == 0){
                        return o2.getKey().compareTo(o1.getKey());
                    } else {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                }
            });
            List<String> stringList = new ArrayList<>();
            for (Map.Entry<String, Double> L : list){
                String[] tempString = L.toString().split("=");
                stringList.add(tempString[0]);
            }
            List<String> result = new ArrayList<>();
            for (int i = 0; i < topK; i++){
                result.add(stringList.get(i));
            }
            return result;
        }
        if (Objects.equals(by, "participants")){
            // Find all the titles.
            List<String> titleList = new ArrayList<>();
            for (Course e : courses){
                if (!titleList.contains(e.getTitle())){
                    titleList.add(e.getTitle());
                }
            }
            Integer[] participants = new Integer[titleList.size()];
            Arrays.fill(participants, 0);
            for (String t : titleList){
                for (Course e : courses){
                    if (Objects.equals(e.getTitle(), t)){
                        if (e.getParticipants() > participants[titleList.indexOf(t)]){
                            participants[titleList.indexOf(t)] = e.getParticipants();
                        }
                    }
                }
            }
            Map<String, Integer> temp = new HashMap<>();
            for (String t : titleList){
                temp.put(t, participants[titleList.indexOf(t)]);
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(temp.entrySet());
            list.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    if (o2.getValue().compareTo(o1.getValue()) == 0){
                        return o2.getKey().compareTo(o1.getKey());
                    } else {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                }
            });
            List<String> stringList = new ArrayList<>();
            for (Map.Entry<String, Integer> L : list){
                String[] tempString = L.toString().split("=");
                stringList.add(tempString[0]);
            }
            List<String> result = new ArrayList<>();
            for (int i = 0; i < topK; i++){
                result.add(stringList.get(i));
            }
            return result;
        }
        return null;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> temp = new ArrayList<>();
        for (Course e : courses){
            if (!temp.contains(e.getTitle())){
                if (e.getPercentAudited() >= percentAudited){
                    if (e.getTotalHours() <= totalCourseHours){
                        if (e.getSubject().toLowerCase().contains(courseSubject.toLowerCase())){
                            temp.add(e.getTitle());
                        }
                    }
                }
            }
        }
        Object[] array = temp.toArray();
        Arrays.sort(array);
        List<String> result = new ArrayList<>();
        for (Object a : array){
            result.add(a.toString());
        }
        return result;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        // Find the required data to calculate similarity.
        Map<String, Double> aveAge = courses.stream().collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getMedianAge)));
        Map<String, Double> aveMale = courses.stream().collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getPercentMale)));
        Map<String, Double> aveDegree = courses.stream().collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getPercentDegree)));
        Map<String, Optional<Course>> latestDate = courses.stream().collect(Collectors.groupingBy(Course::getNumber, Collectors.maxBy(Comparator.comparing(Course::getLaunchDate))));

        // Find similarity for each course.
        Map<String, Double> mapOfSimilarity = new HashMap<>();
        for (Course e : courses){
            double similarity = Math.pow(age - aveAge.get(e.getNumber()), 2)
                + Math.pow(gender * 100 - aveMale.get(e.getNumber()), 2)
                + Math.pow(isBachelorOrHigher * 100 - aveDegree.get(e.getNumber()), 2);
            String title = null;
            if (latestDate.get(e.getNumber()).isPresent()){
                title = latestDate.get(e.getNumber()).get().getTitle();
            }
            if (title == null){
                continue;
            }
            if (mapOfSimilarity.containsKey(title) && mapOfSimilarity.get(title) < similarity){
                continue;
            }
            mapOfSimilarity.put(title, similarity);
        }

        // Sort the map by similarity.
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(mapOfSimilarity.entrySet());
        list.sort(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o2.getValue().compareTo(o1.getValue()) == 0){
                    return -o2.getKey().compareTo(o1.getKey());
                } else {
                    return -o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        List<String> stringList = new ArrayList<>();
        for (Map.Entry<String, Double> L : list){
            String[] tempString = L.toString().split("=");
            stringList.add(tempString[0]);
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            result.add(stringList.get(i));
        }
        return result;
    }
}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }
    public int getParticipants() {
        return participants;
    }
    public String getSubject() {
        return subject;
    }
    public String getTitle() {
        return title;
    }
    public String getInstructors() {
        return instructors;
    }
    public double getTotalHours() {
        return totalHours;
    }
    public double getPercentAudited() {
        return percentAudited;
    }
    public double getMedianAge() {
        return medianAge;
    }
    public Date getLaunchDate() {
        return launchDate;
    }
    public double getPercentDegree() {
        return percentDegree;
    }
    public double getPercentMale() {
        return percentMale;
    }
    public String getNumber() {
        return number;
    }
}

class CourseForQ2 {
    String institution_CourseSubject;
    int participants;

    public CourseForQ2(String institution_CourseSubject, int participants){
        this.institution_CourseSubject = institution_CourseSubject;
        this.participants = participants;
    }

    public String getInstitution_CourseSubject() {
        return institution_CourseSubject;
    }

    public int getParticipants() {
        return participants;
    }
}
