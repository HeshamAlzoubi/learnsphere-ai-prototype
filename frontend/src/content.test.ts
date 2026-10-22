import { describe, expect, it } from 'vitest';
import { lessonContent, recommendationText } from './content';
describe('synthetic lesson content',()=>{
 it('provides distinct content for all three lessons',()=>{expect(lessonContent('lesson-001')).toHaveLength(3);expect(lessonContent('lesson-002')[0].title).toBe('Conditions');expect(lessonContent('lesson-003')[0].title).toBe('Repeating instructions');});
 it('does not invent content for missing lessons',()=>expect(lessonContent('missing')).toEqual([]));
 it('explains all recommendation bands',()=>{for(const band of ['REINFORCE','PRACTICE','ADVANCE'])expect(recommendationText(band).length).toBeGreaterThan(15);});
});
