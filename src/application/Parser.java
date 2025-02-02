package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This is a singleton class
 *
 * @author youlianglim
 *
 */
public class Parser {

	private static Parser instance;

	public static Parser getInstance() {
		if (instance == null) {
			instance = new Parser();

		}
		return instance;
	}

	public Command parseCommand(String command) {
		// Need to parse add, see all, change, delete, undo, edit, done,
		// prioritise command

		String cmd, text = "";

		ArrayList<Parameter> parameters = new ArrayList<Parameter>();

		int indexOfFirstSpace = 0;
		int indexOfFirstInvertedSlash = 0;
		boolean passFirstSpace = false;

		// check and find the first space and slash.
		for (int i = 0; i < command.length(); i++) {
			if (command.charAt(i) == ' ' && !passFirstSpace) {
				indexOfFirstSpace = i;
				passFirstSpace = true;
			} else if (command.charAt(i) == '\\') {
				indexOfFirstInvertedSlash = i;
				break;
			}
		}
		
		//check if is a list command with no parameter. e.g list
		if (isCommandType(Command.LIST_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}
		
		if (indexOfFirstSpace == 0) {
			return null;
		}

		cmd = command.substring(0, indexOfFirstSpace);
		int cmdType = mapCommandType(cmd);
		if (indexOfFirstSpace < command.length()) {
			if (isValidCommandWithParameter(indexOfFirstSpace, indexOfFirstInvertedSlash)) {
				text = command.substring(indexOfFirstSpace + 1, indexOfFirstInvertedSlash);
				String parameterStr = command.substring(indexOfFirstInvertedSlash, command.length());
				String[] parameterArr = parameterStr.split(" ");
				extractParameter(parameters, parameterArr);
			} else {

				text = command.substring(indexOfFirstSpace + 1, command.length());
			}
		}
		return new Command(cmdType, text.trim(), parameters);
	}

	/**
	 * @param indexOfFirstSpace
	 * @param indexOfFirstInvertedSlash
	 * @return
	 */
	private boolean isValidCommandWithParameter(int indexOfFirstSpace, int indexOfFirstInvertedSlash) {
		return indexOfFirstInvertedSlash > 0 && indexOfFirstInvertedSlash > indexOfFirstSpace;
	}

	/**
	 * This function will convert raw parameter and add it into arraylist of parameter.
	 * @param parameters : arraylist to store extracted parameter
	 * @param parameterArr : raw parameter string to convert into parameter instance.
	 */
	private void extractParameter(ArrayList<Parameter> parameters, String[] parameterArr) {
		for (int i = 0; i < parameterArr.length; i++) {
			if (parameterArr[i].charAt(0) == '\\') {
				// TODO : Check whether if the arguments is in correct
				// format.
				String para = parameterArr[i].substring(1, parameterArr[i].length());
				//If the next parameter charAt(0) is not \\, then it going to concat together
				// with this paragraph
				
				String paraContent = "";
				
				for(int c =i + 1; c < parameterArr.length; c++){
					if( parameterArr[c].charAt(0) != '\\'){
						paraContent += parameterArr[c].substring(0, parameterArr[c].length()) + " ";
					}
					else
					{
						break;
					}
					
				}
				
				parameters.add(new Parameter(mapParameterType(para), paraContent.trim()));
			}
		}
	}

	private Integer mapCommandType(String cmd) {

		if (isCommandType(Command.ADD_COMMAND, cmd)) {
			return Command.ADD_COMMAND_TYPE;
		} else if (isCommandType(Command.CHANGE_STORAGE_COMMAND, cmd)) {
			return Command.CHANGE_STORAGE_COMMAND_TYPE;
		} else if (isCommandType(Command.DELETE_COMMAND, cmd)) {
			return Command.DELETE_COMMAND_TYPE;
		} else if (isCommandType(Command.DONE_COMMAND, cmd)) {
			return Command.DONE_COMMAND_TYPE;
		} else if (isCommandType(Command.EDIT_COMMAND, cmd)) {
			return Command.EDIT_COMMAND_TYPE;
		} else if (isCommandType(Command.LIST_COMMAND, cmd)) {
			return Command.LIST_COMMAND_TYPE;
		} else if (isCommandType(Command.UNDO_COMMAND, cmd)) {
			return Command.UNDO_COMMAND_TYPE;
		} else {
			return -1;
		}

	}

	private boolean isCommandType(String cmdDefinition, String cmd) {

		String[] cmdDef = cmdDefinition.split(":");
		for (int i = 0; i < cmdDef.length; i++) {
			if (cmdDef[i].equals(cmd)) {
				return true;
			}
		}
		return false;

	}

	private Integer mapParameterType(String parameterStr) {

		switch (parameterStr) {

		case Parameter.PRIORITY_ARGUMENT:
			return Parameter.PRIORITY_ARGUMENT_TYPE;

		case Parameter.START_DATE_ARGUMENT:
			return Parameter.START_DATE_ARGUMENT_TYPE;

		case Parameter.PLACE_ARGUMENT:
			return Parameter.PLACE_ARGUMENT_TYPE;
		case Parameter.END_DATE_ARGUMENT:
			return Parameter.END_DATE_ARGUMENT_TYPE;
		case Parameter.TYPE_ARGUMENT:
			return Parameter.TYPE_ARGUMENT_TYPE;

		default:
			return -1;

		}

	}

	public Task convertAddCommandtoTask(Command cmd){

		Task task = new Task(cmd.getTextContent());

		ArrayList<Parameter> lists = cmd.getParameter();
		
		DateFormat df1 = new SimpleDateFormat(TasksFormatter.DATE_FORMAT_TYPE_1);
		

		for(Parameter para: lists){
			if(para.getParaType() == Parameter.START_DATE_ARGUMENT_TYPE){

				try {
					task.setStart_date(df1.parse(para.getParaArg()));
				} catch (ParseException e) {
					task.setStart_date(null);
					e.printStackTrace();
				}

			}else if(para.getParaType() == Parameter.END_DATE_ARGUMENT_TYPE){
				try {
					task.setEnd_date(df1.parse(para.getParaArg()));
				} catch (ParseException e) {
					task.setEnd_date(null);
					e.printStackTrace();
				}
			}
			else if(para.getParaType() == Parameter.PLACE_ARGUMENT_TYPE){
				task.setPlace_argument(para.getParaArg());
			}else if(para.getParaType() == Parameter.PRIORITY_ARGUMENT_TYPE){
				task.setPriority_argument(para.getParaArg());
			}else if(para.getParaType() == Parameter.TYPE_ARGUMENT_TYPE){
				task.setType_argument(para.getParaArg());
			}
		}

		return task;

	}

}
