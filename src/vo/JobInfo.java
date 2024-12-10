package vo;

public class JobInfo {
    private String title;       // 职位名称
    private String company;     // 公司名称
    private String salary;      // 月薪范围
    private String location;    // 工作地点
    private String experience;  // 经验要求
    private String education;   // 学历要求
    private String headcount;   // 招聘人数
    private String publishDate; // 发布日期

    // 构造函数
    public JobInfo(String title, String company, String salary, String location, 
                  String experience, String education, String headcount, String publishDate) {
        this.title = title;
        this.company = company;
        this.salary = salary;
        this.location = location;
        this.experience = experience;
        this.education = education;
        this.headcount = headcount;
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return String.format("职位: %s\n公司: %s\n薪资: %s\n地点: %s\n经验要求: %s\n学历要求: %s\n招聘人数: %s\n发布日期: %s\n",
                title, company, salary, location, experience, education, headcount, publishDate);
    }

    // getter和setter方法
    public String getTitle() {
        return title;
    }
    public String getCompany() {
        return company;
    }

    public String getSalary() {
        return salary;
    }
    
    public String getLocation() {
        return location;
    }

    public String getExperience() {
        return experience;
    }

    public String getEducation() {
        return education;
    }

    public String getHeadcount() {
        return headcount;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }   

    public void setEducation(String education) {
        this.education = education;
    }   

    public void setHeadcount(String headcount) {
        this.headcount = headcount;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

} 