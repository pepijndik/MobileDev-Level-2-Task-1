package nl.pdik.level2.task1

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.pdik.level2.task1.model.Statement
import nl.pdik.level2.task1.ui.theme.Level2Task1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Level2Task1Theme {
                // A surface container using the 'background' color from the theme
                Surface(

                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    QuizzScaffold()
                }
            }
        }
    }
}

@Composable
fun QuizzScaffold() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        content = {padding -> ScreenContent(Modifier.padding(padding))  }
    )
}

@Composable
private fun ScreenContent(modifier: Modifier) {
    // ArrayList to hold the predefined quiz statements. Also, initialize it.
    val quizStatements: MutableList<Statement> = remember { mutableStateListOf() }
    quizStatements.addAll(generateStatements())
    val context = LocalContext.current

    Column {
        Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            QuizInstructionsHeader()
            AddQuizStatement(addQuizStatement = { statement ->
                if(statement.statement.isNotBlank()) {
                    quizStatements.add(statement);
                    informUser(context,R.string.new_quiz_statement)
                }else{
                    informUser(context,R.string.not_empty)
                }
            })
        }
        QuizStatements(quizStatements,
            removeQuizStatement = { statement: Statement ->
                quizStatements.remove(statement)
            },
        context
        )
    }
}

@Composable
private fun QuizInstructionsHeader(){
    Text(
        text = stringResource(id = R.string.quiz_instr_header),
        style = MaterialTheme.typography.h4
    )

    Text(
        text = stringResource(id = R.string.quiz_instr_description),
        style = MaterialTheme.typography.body1
    )
}

@Composable
private fun QuizStatements(localQuizStatements: MutableList<Statement>,
                           removeQuizStatement: (Statement) -> Unit,
                            context: Context) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items=localQuizStatements,itemContent = { quizStatement ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {

                        },
                        onDoubleTap = { /* Called on Double Tap */ },
                        onLongPress = {
                            if (!quizStatement.isTrue) {
                                removeQuizStatement(quizStatement);
                                informUser(context, R.string.answer_is_false)
                            } else {
                                informUser(context, R.string.wrong_answer)
                            }
                        },
                        onTap = {
                            if (!quizStatement.isTrue) {
                                informUser(context, R.string.wrong_answer);
                            } else {
                                removeQuizStatement(quizStatement);
                                informUser(context, R.string.answer_is_true)
                            }
                        }
                    )
                }) {
                Text(
                    modifier = Modifier
                        .padding(16.dp),
                    text = quizStatement.statement,
                )
            }
            Divider(
                color = Color.LightGray, modifier = Modifier.alpha(0.6f),
                thickness = 1.dp
            )
        })
    }
}


@Composable
private fun AddQuizStatement(addQuizStatement: (Statement) -> Unit) {
    var statementText by remember {
        mutableStateOf("")
    }
     Column() {
        OutlinedTextField(
            value = statementText,
            onValueChange = { statementText = it },
            label = { Text(text = stringResource(id = R.string.new_quiz_statement)) }
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff4caf50)),
                onClick = {
                          addQuizStatement(Statement(statementText,true))
                    statementText= "";
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(100.dp),
            ) {
                Text(text = stringResource(id = R.string.button_true))
            }
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                onClick = {
                    addQuizStatement(Statement(statementText,false))
                    statementText= "";
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(100.dp),
            ) {
                Text(text = stringResource(id = R.string.button_false))
            }
        }

}
private fun informUser(context: Context, msgId: Int) {
    Toast.makeText(context, context.getString(msgId), Toast.LENGTH_SHORT).show()
}
/**
 * Give back an arraylist with statements. For now hardcoded. Later in the course we
 * are going to work with dynamic data.
 */
private fun generateStatements(): ArrayList<Statement> {
    return arrayListOf(
        Statement("A \'val\' and \'var\' are the same.", false),
        Statement("Mobile Application Development grants 12 ECTS.", false),
        Statement("A unit in Kotlin corresponds to a void in Java.", true),
        Statement("In Kotlin \'when\' replaces the \'switch\' operator in Java.", true)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Level2Task1Theme {
        QuizzScaffold()
    }
}