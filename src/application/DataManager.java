package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.*;

/*
 * @author: Lim Qi Wen
 */

public class DataManager {
	public static final Integer MULTIPLE_MATCHES = -2;
	public static final Integer TASK_NOT_FOUND = -1;
	public static final Integer TASK_REMOVED = 1;
	public static final Integer TASK_SET_TO_DONE = 2;
	public static final Integer TASK_UPDATED = 3;
	
	
	private LocalStorage file;
	private Gson gson;
	private ArrayList<Task> taskList;
	private ArrayList<Task> searchList;
	public static DataManager instance = null;

	private DataManager() {
		file = new LocalStorage();
		gson = new Gson();
		taskList = initialiseTaskList();
		searchList = new ArrayList<Task>();
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public void addNewTask(Task taskToAdd) {
		searchList.clear();
		taskList.add(taskToAdd);
		sort();
		file.clear();
		file.saveToFile(tasksToStrings());
	}

	public ArrayList<Task> listAll(String para) {
		searchList.clear();
		/*
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0; i<taskList.size();i++){
			list.add(taskList.get(i).getTextContent());
			//TODO filter list by parameter;
		}
		return list;
		*/
		return taskList;
	}

	public Integer removeTask(Command cmd) {
		searchList.clear();
		searchTasksForMatches(cmd);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:	
				taskList.remove(searchList.get(0));
				file.clear();
				file.saveToFile(tasksToStrings());
				return TASK_REMOVED;
			default:
				LogicController.getInstance().chooseLine(tasksToStrings(searchList));
				return MULTIPLE_MATCHES;
		}	
	}
	
	public Integer removeTask(int lineNum){
		taskList.remove(searchList.get(lineNum));
		file.clear();
		file.saveToFile(tasksToStrings());
		return TASK_REMOVED;
	}

	public Integer editTask(Command cmd) {
		searchList.clear();
		searchTasksForMatches(cmd);

		DateFormat df1 = new SimpleDateFormat(TasksFormatter.DATE_FORMAT_TYPE_1);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:	
				ArrayList<Parameter> para = new ArrayList<Parameter>();
				para = cmd.getParameter();
				for(int i=0; i<para.size(); i++){
					switch(para.get(i).getParaType()){
						case Parameter.PRIORITY_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setPriority_argument(para.get(i).getParaArg());
							break;
						case Parameter.TYPE_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setType_argument(para.get(i).getParaArg());
							break;
						case Parameter.START_DATE_ARGUMENT_TYPE:
						try {
							taskList.get(taskList.indexOf(searchList.get(0))).setStart_date(df1.parse(para.get(i).getParaArg()));
						} catch (ParseException e) {
							// Do nothing, task remain the same.
							e.printStackTrace();
						}
							break;
						case Parameter.END_DATE_ARGUMENT_TYPE:
							try {
								taskList.get(taskList.indexOf(searchList.get(0))).setEnd_date(df1.parse(para.get(i).getParaArg()));
							} catch (ParseException e) {
								// Do nothing, task remain the same.
								e.printStackTrace();
							}
							break; 
						default:
							taskList.get(taskList.indexOf(searchList.get(0))).setPlace_argument(para.get(i).getParaArg());
							break;
					}
				}
				file.clear();
				file.saveToFile(tasksToStrings());
				return TASK_UPDATED;
			default:
				LogicController.getInstance().chooseLine(tasksToStrings(searchList));
				return MULTIPLE_MATCHES;
		}	
	}
	
	public Integer editTask(int lineNum){
		//TODO
		return TASK_UPDATED;
	}

	public Integer setDoneToTask(Command cmd){
		searchList.clear();
		searchTasksForMatches(cmd);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:	
				int index = taskList.indexOf(searchList.get(0));
				taskList.get(index).setDone(true);
				file.clear();
				file.saveToFile(tasksToStrings());
				return TASK_SET_TO_DONE;
			default:
				LogicController.getInstance().chooseLine(tasksToStrings(searchList));
				return MULTIPLE_MATCHES;
		}	
	}
	
	public Integer setDoneToTask(int lineNum){
		int index = taskList.indexOf(searchList.get(lineNum));
		taskList.get(index).setDone(true);
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		return file.changePath(cmd.getTextContent());
	}
	
	private ArrayList<String> tasksToStrings(){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<taskList.size();i++){
			taskStrings.add(gson.toJson(taskList.get(i)));
		}
		return taskStrings;
	}
	
	private ArrayList<String> tasksToStrings(ArrayList<Task> list){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			taskStrings.add(list.get(i).getTextContent());
		}
		return taskStrings;
	}
	
	private void sort(){
		Collections.sort(taskList);
	}
	
	private ArrayList<Task> initialiseTaskList(){
		ArrayList<Task> list = new ArrayList<Task>();
		ArrayList<String> stringList = file.readFile();
		for(int i=0; i<stringList.size();i++){
			list.add(gson.fromJson(stringList.get(i), Task.class));
		}
		return list;
	}

	private void searchTasksForMatches(Command cmd) {
		for(int i=0; i<taskList.size();i++){
			if(taskList.get(i).getTextContent().contains(cmd.getTextContent())){
				searchList.add(taskList.get(i));
			}
		}
	}
}

