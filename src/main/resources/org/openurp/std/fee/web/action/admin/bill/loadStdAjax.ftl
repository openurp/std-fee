[#ftl]
{
  "id": ${(std.id)!"null"}, "user": { "name": "${(std.name)!}" }, "level": { "name": "${(std.level.name)!}" }, "state": { "major": { "name": "${(std.state.major.name)!}" }, "department": { "name": "${(std.state.department.name)!}" }, "squad": { "name": "${(std.state.squad.name)!}" } }
}
