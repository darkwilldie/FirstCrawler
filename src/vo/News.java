package vo;

public class News {
	private String id;
	private String title;  //新闻标题
	private String tongxunyuan;  //通讯员
	private String laiyuan;  //来源
	private int click;  //点击数
	private String fabushijian;  //发布时间
	private String shengao; //审稿人
	private String editor;  //编辑
	public News() {
		super();
	}
	public News(String id, String title, String tongxunyuan, String laiyuan, int click, String fabushijian,
			String shengao, String editor) {
		super();
		this.id = id;
		this.title = title;
		this.tongxunyuan = tongxunyuan;
		this.laiyuan = laiyuan;
		this.click = click;
		this.fabushijian = fabushijian;
		this.shengao = shengao;
		this.editor = editor;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTongxunyuan() {
		return tongxunyuan;
	}
	public void setTongxunyuan(String tongxunyuan) {
		this.tongxunyuan = tongxunyuan;
	}
	public String getLaiyuan() {
		return laiyuan;
	}
	public void setLaiyuan(String laiyuan) {
		this.laiyuan = laiyuan;
	}
	public int getClick() {
		return click;
	}
	public void setClick(int click) {
		this.click = click;
	}
	public String getFabushijian() {
		return fabushijian;
	}
	public void setFabushijian(String fabushijian) {
		this.fabushijian = fabushijian;
	}
	public String getShengao() {
		return shengao;
	}
	public void setShengao(String shengao) {
		this.shengao = shengao;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	@Override
	public String toString() {
		return id + "|" + title + "|" + tongxunyuan + "|" + laiyuan + "|" + click + "|" + fabushijian + "|"
				+ shengao + "|" + editor;
	}
	
	
	
	
	
	
	

}
