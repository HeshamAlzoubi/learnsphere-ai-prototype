/** Synthetic lesson explanations; assessment option labels are served by the backend. */
const lessons: Record<string,{title:string;body:string}[]> = {
 'lesson-001':[
  {title:'Sequence',body:'An algorithm is a clear series of steps to solve a problem. Computers follow instructions in the order given.'},
  {title:'Selection',body:'A condition lets an algorithm choose what to do next, such as: if it rains, take an umbrella.'},
  {title:'Repetition',body:'A loop repeats steps. For example, repeat a move five times instead of writing it five times.'}],
 'lesson-002':[
  {title:'Conditions',body:'A condition is a statement that evaluates to true or false.'},
  {title:'If',body:'An if statement runs a set of instructions only when its condition is true.'},
  {title:'Else',body:'An else branch provides an alternative when the if condition is false.'}],
 'lesson-003':[
  {title:'Repeating instructions',body:'Loops let programs repeat a sequence of steps.'},
  {title:'Counting repetitions',body:'A counted loop can repeat its body a specified number of times.'},
  {title:'Stopping conditions',body:'A stopping condition helps a loop finish instead of repeating indefinitely.'}]
};
export function lessonContent(id:string){return lessons[id]??[];}
export function recommendationText(band:string):string {
 switch(band){case 'REINFORCE':return 'Review the lesson, then try another practice round.';case 'PRACTICE':return 'You are making progress. Practice the concepts again before advancing.';case 'ADVANCE':return 'Strong work! You are ready for the next topic when it becomes available.';default:return 'Your recommendation is shown above.';}
}
