const $ = id => document.getElementById(id);
async function refresh() {
  const response = await fetch('/api/state');
  if (!response.ok) throw new Error('Unable to load course state');
  const data = await response.json();
  $('seats').textContent = `${data.enrolled.length} / ${data.capacity}`;
  $('waiting').textContent = data.waitlisted.length;
  for (const key of ['enrolled', 'waitlisted']) {
    const list = $(key);
    list.replaceChildren();
    for (const learner of data[key]) {
      const li = document.createElement('li');
      li.textContent = learner;
      list.append(li);
    }
    if (!data[key].length) {
      const li = document.createElement('li');
      li.textContent = 'None';
      list.append(li);
    }
  }
}
async function submit(action, completed) {
  const learner = $('learner').value.trim();
  const params = new URLSearchParams({ learner });
  if (completed !== undefined) params.set('completed', String(completed));
  try {
    const response = await fetch(`/api/${action}`, {method:'POST',body:params});
    const data = await response.json();
    $('message').textContent = data.message ? `${data.code}: ${data.message}` : `Error: ${data.error}`;
    await refresh();
  } catch (error) { $('message').textContent = `Network error: ${error.message}`; }
}
$('complete').addEventListener('click', () => submit('prerequisite',true));
$('revoke').addEventListener('click', () => submit('prerequisite',false));
$('enroll').addEventListener('click', () => submit('enroll'));
$('drop').addEventListener('click', () => submit('drop'));
refresh().catch(error => $('message').textContent = error.message);
