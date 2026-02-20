class Holiday {
  name = "";
  id = "";
}

export class PublicHoliday extends Holiday {
  date = new Date();
}

export class SchoolHoliday extends Holiday {
  start = new Date();
  end = new Date();
}
